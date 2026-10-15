package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.*;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.NotEnoughRecipesException;
import com.e.mealtracker.exception.ResourceNotFoundException;
import com.e.mealtracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.e.mealtracker.dto.FillerSuggestionDto;
import com.e.mealtracker.dto.FillSuggestionsResponse;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    private final NutritionCalculationService nutritionCalculationService;

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
    @Transactional(readOnly = true)
    public DayTotals calculateDayTotals(Long planId, LocalDate date, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + planId));

        List<MealPlanItem> items = mealPlanItemRepository.findAllByPlanId(plan.getId());

        BigDecimal calories = BigDecimal.ZERO;
        BigDecimal proteins = BigDecimal.ZERO;

        for (MealPlanItem item : items) {
            if (!date.equals(item.getPlanDate())) continue;

            if (item.getRecipe() != null) {
                BigDecimal servings = BigDecimal.valueOf(
                        item.getServings() != null ? item.getServings() : 1.0);
                calories = calories.add(item.getRecipe().calculateTotalCalories().multiply(servings));
                proteins = proteins.add(item.getRecipe().calculateTotalProteins().multiply(servings));
            } else if (item.getIngredient() != null) {
                double grams = item.getWeightInGrams() != null ? item.getWeightInGrams() : 100.0;
                Ingredient ing = item.getIngredient();
                calories = calories.add(
                        BigDecimal.valueOf(ing.calculateCaloriesPer100g())
                                .multiply(BigDecimal.valueOf(grams))
                                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                if (ing.getProteinsPer100g() != null) {
                    proteins = proteins.add(
                            BigDecimal.valueOf(ing.getProteinsPer100g())
                                    .multiply(BigDecimal.valueOf(grams))
                                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP));
                }
            }
        }

        return new DayTotals(calories.setScale(2, RoundingMode.HALF_UP),
                proteins.setScale(2, RoundingMode.HALF_UP));
    }

    private void checkRecipesAvailability(User user, List<MealType> requiredCategories) {
        List<MealType> missing = new ArrayList<>();

        for (MealType category : requiredCategories) {
            List<Recipe> recipes = recipeRepository.findAllByUserAndCategory(user, category);
            if (recipes.isEmpty()) {
                missing.add(category);
            }
        }

        if (!missing.isEmpty()) {
            throw new NotEnoughRecipesException(missing);
        }
    }
    public MealPlanDto generateDraft(String name, LocalDate startDate, LocalDate endDate, String username) {
        User user = getUser(username);

        if (startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate не может быть позже endDate");
        }

        List<MealType> categories = List.of(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER);

        // 1. Проверка рецептов
        checkRecipesAvailability(user, categories);

        // 2. Создание плана
        MealPlan plan = new MealPlan();
        plan.setUser(user);
        plan.setName(name);
        plan.setStartDate(startDate);
        plan.setEndDate(endDate);
        plan = mealPlanRepository.save(plan);

        List<MealPlanItem> items = new ArrayList<>();

        // 3. Проход по дням
        LocalDate date = startDate;
        while (!date.isAfter(endDate)) {
            for (MealType category : categories) {
                List<Recipe> recipes = recipeRepository.findAllByUserAndCategory(user, category);
                Recipe randomRecipe = recipes.get(
                        ThreadLocalRandom.current().nextInt(recipes.size()));

                MealPlanItem item = new MealPlanItem();
                item.setPlan(plan);
                item.setPlanDate(date);
                item.setMealType(category);
                item.setRecipe(randomRecipe);
                item.setServings(1.0);

                items.add(mealPlanItemRepository.save(item));
            }
            date = date.plusDays(1);
        }

        // 4. Возврат DTO
        List<MealPlanItemDto> itemDtos = items.stream()
                .map(MealPlanItemDto::fromEntity)
                .toList();

        return MealPlanDto.fromEntity(plan, itemDtos);
    }

    public MealPlanItemDto replaceItem(Long planId, Long itemId, Long newRecipeId, String username) {
        User user = getUser(username);

        MealPlan plan = mealPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + planId));

        MealPlanItem item = mealPlanItemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException("Элемент не найден: " + itemId));

        if (!item.getPlan().getId().equals(plan.getId())) {
            throw new IllegalArgumentException("Элемент не принадлежит плану");
        }

        Recipe newRecipe = recipeRepository.findById(newRecipeId)
                .orElseThrow(() -> new ResourceNotFoundException("Рецепт не найден: " + newRecipeId));

        if (newRecipe.getCategory() != item.getMealType()) {
            throw new IllegalArgumentException(
                    "Категория рецепта (" + newRecipe.getCategory() +
                            ") не совпадает с приёмом (" + item.getMealType() + ")");
        }

        item.setRecipe(newRecipe);
        item = mealPlanItemRepository.save(item);

        return MealPlanItemDto.fromEntity(item);
    }
    @Transactional(readOnly = true)
    public FillSuggestionsResponse suggestFillers(Long planId, LocalDate date, String username) {
        User user = getUser(username);
        MealPlan plan = mealPlanRepository.findByIdAndUser(planId, user)
                .orElseThrow(() -> new ResourceNotFoundException("План не найден: " + planId));

        // 1. Текущее состояние дня
        DayTotals current = calculateDayTotals(planId, date, username);

        // 2. Цели
        BigDecimal targetCalories = nutritionCalculationService.calculateDailyCaloriesForUser(user);
        double targetProtein = nutritionCalculationService
                .calculateTargetProteinFromGoals(user)
                .targetProteinGramsPerDay();

        // 3. Дельта
        BigDecimal caloriesNeeded = targetCalories.subtract(current.calories())
                .setScale(2, RoundingMode.HALF_UP);
        BigDecimal proteinsNeeded = BigDecimal.valueOf(targetProtein)
                .subtract(current.proteins())
                .setScale(2, RoundingMode.HALF_UP);

        if (caloriesNeeded.compareTo(BigDecimal.ZERO) <= 0) {
            return new FillSuggestionsResponse(
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP),
                    List.of());
        }

        // 4. Кандидаты — только категории добора
        List<String> fillerCategories = List.of("DAIRY", "FRUITS", "NUTS", "SWEETS", "EGGS");

        List<Ingredient> candidates = ingredientRepository
                .findAllByUsernameIn(List.of(Ingredient.SYSTEM_USERNAME, username))
                .stream()
                .filter(i -> i.getCategory() != null)
                .filter(i -> fillerCategories.contains(i.getCategory()))
                .toList();

        // 5. Считаем score и порцию для каждого
        List<FillerSuggestionDto> all = new ArrayList<>();
        for (Ingredient ing : candidates) {
            double calsPer100 = ing.calculateCaloriesPer100g();
            if (calsPer100 <= 0) continue;

            // Порция по плотности ккал
            double portion;
            if (calsPer100 > 300) portion = 30;
            else if (calsPer100 > 100) portion = 100;
            else portion = 200;

            BigDecimal weight = BigDecimal.valueOf(portion);
            BigDecimal cals = BigDecimal.valueOf(calsPer100)
                    .multiply(weight)
                    .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

            BigDecimal proteins = BigDecimal.ZERO;
            if (ing.getProteinsPer100g() != null) {
                proteins = BigDecimal.valueOf(ing.getProteinsPer100g())
                        .multiply(weight)
                        .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            }

            all.add(new FillerSuggestionDto(
                    ing.getId(),
                    ing.getName(),
                    ing.getCategory(),
                    weight,
                    cals,
                    proteins));
        }

        // 6. Сортировка по score + макс 1 на категорию
        List<FillerSuggestionDto> sorted = all.stream()
                .sorted((a, b) -> {
                    double scoreA = a.calories().doubleValue() / 200 + a.proteins().doubleValue() / 10;
                    double scoreB = b.calories().doubleValue() / 200 + b.proteins().doubleValue() / 10;
                    return Double.compare(scoreB, scoreA);
                })
                .toList();

        List<FillerSuggestionDto> suggestions = new ArrayList<>();
        Set<String> usedCategories = new HashSet<>();
        for (FillerSuggestionDto s : sorted) {
            if (usedCategories.contains(s.category())) continue;
            usedCategories.add(s.category());
            suggestions.add(s);
            if (suggestions.size() >= 5) break;
        }

        return new FillSuggestionsResponse(caloriesNeeded, proteinsNeeded, suggestions);
    }

    private User getUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));
    }
}
