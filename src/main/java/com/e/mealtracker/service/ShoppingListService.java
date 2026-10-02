package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.*;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShoppingListService {

    private final ShoppingListRepository shoppingListRepository;
    private final ShoppingListItemRepository shoppingListItemRepository;
    private final MealPlanRepository mealPlanRepository;
    private final MealPlanItemRepository mealPlanItemRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<ShoppingListDto> getAllLists(String username) {
        User user = getUser(username);
        return shoppingListRepository.findAllByUserAndStatusOrderByCreatedAtDesc(user, "ACTIVE").stream()
                .map(this::toDto)
                .toList();
    }

    @Transactional(readOnly = true)
    public ShoppingListDto getList(Long id, String username) {
        User user = getUser(username);
        ShoppingList list = shoppingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Список не найден: " + id));
        return toDto(list);
    }

    /**
     * Генерирует список покупок из плана за указанный период.
     * Если активный список уже есть — архивирует его.
     */
    public ShoppingListDto generateFromPlan(Long planId, LocalDate periodStart, LocalDate periodEnd, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + planId));

        // Архивируем старый активный
        shoppingListRepository.findFirstByUserAndStatus(user, "ACTIVE")
                .ifPresent(old -> {
                    old.setStatus("ARCHIVED");
                    shoppingListRepository.save(old);
                });

        // Создаём новый
        ShoppingList list = new ShoppingList();
        list.setUser(user);
        list.setPlan(plan);
        list.setName("Список покупок");
        list.setPeriodStart(periodStart != null ? periodStart : plan.getStartDate());
        list.setPeriodEnd(periodEnd != null ? periodEnd : plan.getEndDate());
        list.setStatus("ACTIVE");
        list = shoppingListRepository.save(list);

        // Собираем ингредиенты
        Map<Long, AggregatedItem> aggregate = new HashMap<>();

        List<MealPlanItem> items = mealPlanItemRepository
                .findAllByPlanIdAndPlanDateBetween(planId, list.getPeriodStart(), list.getPeriodEnd());

        for (MealPlanItem item : items) {
            if (item.getRecipe() != null) {
                // Рецепт → разворачиваем в ингредиенты
                Recipe recipe = item.getRecipe();
                double totalWeight = recipe.getTotalWeight();
                if (totalWeight <= 0) continue;

                double servings = item.getServings() != null ? item.getServings() : 1.0;
                double portionWeight = (totalWeight / (recipe.getServings() != null ? recipe.getServings() : 1)) * servings;

                for (RecipeIngredient ri : recipe.getIngredients()) {
                    double grams = (ri.getWeightInGrams() / totalWeight) * portionWeight;
                    addToAggregate(aggregate, ri.getIngredient(), grams);
                }
            } else if (item.getIngredient() != null) {
                // Ингредиент напрямую
                addToAggregate(aggregate, item.getIngredient(),
                        item.getWeightInGrams() != null ? item.getWeightInGrams() : 100.0);
            }
        }

        // Сохраняем items
        for (AggregatedItem agg : aggregate.values()) {
            ShoppingListItem slItem = new ShoppingListItem();
            slItem.setList(list);
            slItem.setIngredient(agg.ingredient);
            slItem.setIngredientName(agg.ingredient != null ? agg.ingredient.getName() : agg.customName);
            slItem.setCategory(agg.ingredient != null && agg.ingredient.getCategory() != null
                    ? agg.ingredient.getCategory() : "OTHER");
            slItem.setQuantityGrams(agg.totalGrams);
            slItem.setUnitType(agg.ingredient != null ? agg.ingredient.getUnitType() : null);
            slItem.setIsChecked(false);
            shoppingListItemRepository.save(slItem);
        }

        return toDto(list);
    }

    public ShoppingListItemDto toggleItem(Long listId, Long itemId, String username) {
        User user = getUser(username);
        ShoppingList list = shoppingListRepository.findByIdAndUser(listId, user)
                .orElseThrow(() -> new ResourceNotFoundException("Список не найден: " + listId));
        ShoppingListItem item = shoppingListItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент не найден: " + itemId));
        if (!item.getList().getId().equals(list.getId())) {
            throw new IllegalArgumentException("Элемент не принадлежит списку");
        }
        item.setIsChecked(!Boolean.TRUE.equals(item.getIsChecked()));
        item = shoppingListItemRepository.save(item);
        return ShoppingListItemDto.fromEntity(item);
    }

    public void archiveList(Long id, String username) {
        User user = getUser(username);
        ShoppingList list = shoppingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Список не найден: " + id));
        list.setStatus("ARCHIVED");
        shoppingListRepository.save(list);
    }

    public void deleteList(Long id, String username) {
        User user = getUser(username);
        ShoppingList list = shoppingListRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("Список не найден: " + id));
        shoppingListRepository.delete(list);
    }

    private void addToAggregate(Map<Long, AggregatedItem> map, Ingredient ing, double grams) {
        if (ing == null) return;
        AggregatedItem agg = map.computeIfAbsent(ing.getId(), k -> new AggregatedItem());
        agg.ingredient = ing;
        agg.totalGrams += grams;
    }

    private ShoppingListDto toDto(ShoppingList list) {
        List<ShoppingListItemDto> items = shoppingListItemRepository
                .findAllByListId(list.getId()).stream()
                .map(ShoppingListItemDto::fromEntity)
                .toList();
        return ShoppingListDto.fromEntity(list, items);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }

    private static class AggregatedItem {
        Ingredient ingredient;
        String customName;
        double totalGrams = 0.0;
    }
}
