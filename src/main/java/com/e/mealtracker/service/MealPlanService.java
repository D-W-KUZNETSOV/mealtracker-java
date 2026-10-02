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
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class MealPlanService {

    private final MealPlanRepository mealPlanRepository;
    private final MealPlanItemRepository mealPlanItemRepository;
    private final RecipeRepository recipeRepository;
    private final IngredientRepository ingredientRepository;
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public List<MealPlanDto> getAllPlans(String username) {
        User user = getUser(username);
        return mealPlanRepository.findAllByUserOrderByStartDateDesc(user).stream()
                .map(plan -> {
                    List<MealPlanItemDto> items = mealPlanItemRepository
                            .findAllByPlanId(plan.getId()).stream()
                            .map(MealPlanItemDto::fromEntity)
                            .toList();
                    return MealPlanDto.fromEntity(plan, items);
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public MealPlanDto getPlan(Long id, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + id));
        List<MealPlanItemDto> items = mealPlanItemRepository
                .findAllByPlanId(plan.getId()).stream()
                .map(MealPlanItemDto::fromEntity)
                .toList();
        return MealPlanDto.fromEntity(plan, items);
    }

    public MealPlanDto createPlan(String name, LocalDate startDate, LocalDate endDate, String username) {
        User user = getUser(username);
        MealPlan plan = new MealPlan();
        plan.setUser(user);
        plan.setName(name);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan = mealPlanRepository.save(plan);
        return MealPlanDto.fromEntity(plan, List.of());
    }

    public MealPlanDto updatePlan(Long id, String name, LocalDate startDate, LocalDate endDate, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + id));
        if (name != null) plan.setName(name);
        if (startDate != null) plan.setStartDate(startDate);
        if (endDate != null) plan.setEndDate(endDate);
        plan = mealPlanRepository.save(plan);
        List<MealPlanItemDto> items = mealPlanItemRepository
                .findAllByPlanId(plan.getId()).stream()
                .map(MealPlanItemDto::fromEntity)
                .toList();
        return MealPlanDto.fromEntity(plan, items);
    }

    public void deletePlan(Long id, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(id, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + id));
        mealPlanRepository.delete(plan);
    }

    public MealPlanItemDto addItem(Long planId, LocalDate planDate, String mealType,
                                   Long recipeId, Long ingredientId,
                                   Double servings, Double weightInGrams,
                                   String customName, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + planId));

        MealPlanItem item = new MealPlanItem();
        item.setPlan(plan);
        item.setPlanDate(planDate);
        try {
            item.setMealType(MealType.valueOf(mealType.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неверный тип приёма: " + mealType);
        }

        if (recipeId != null) {
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new ResourceNotFoundException("Рецепт не найден: " + recipeId));
            item.setRecipe(recipe);
            item.setServings(servings != null ? servings : 1.0);
        } else if (ingredientId != null) {
            Ingredient ingredient = ingredientRepository.findById(ingredientId)
                    .orElseThrow(() -> new ResourceNotFoundException("Ингредиент не найден: " + ingredientId));
            item.setIngredient(ingredient);
            item.setWeightInGrams(weightInGrams != null ? weightInGrams : 100.0);
        } else {
            throw new IllegalArgumentException("Нужен recipeId или ingredientId");
        }

        item.setCustomName(customName);
        item = mealPlanItemRepository.save(item);
        return MealPlanItemDto.fromEntity(item);
    }

    public void deleteItem(Long planId, Long itemId, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + planId));
        MealPlanItem item = mealPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент не найден: " + itemId));
        if (!item.getPlan().getId().equals(plan.getId())) {
            throw new IllegalArgumentException("Элемент не принадлежит плану");
        }
        mealPlanItemRepository.delete(item);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
