package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.*;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.InvalidPortionWeightException;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.FoodPortionRequest;
import com.e.mealtracker.repository.IngredientRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class StatsService {

    private final DailyLogRepository dailyLogRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final NutritionCalculationService nutritionCalculationService;
    private final RecipeNutritionService recipeNutritionService;
    private final MealPlanRepository mealPlanRepository;
    private final MealPlanItemRepository mealPlanItemRepository;
    private final IngredientRepository ingredientRepository;


    /**
     * Добавить порцию и вернуть статистику за сегодня.
     * @param portion данные о порции
     * @param user текущий пользователь (должен быть уже аутентифицирован)
     */
    @Transactional
    public DailyStatsDto addPortionAndReturnTodayStats(FoodPortionRequest portion, User user) {
        // 1. Валидация: ровно один из recipeId / ingredientId
        boolean hasRecipe = portion.getRecipeId() != null;
        boolean hasIngredient = portion.getIngredientId() != null;

        if (hasRecipe == hasIngredient) {
            throw new IllegalArgumentException(
                    "Укажите либо recipeId, либо ingredientId (ровно одно)");
        }

        if (portion.getWeightInGrams() <= 0) {
            throw new InvalidPortionWeightException(portion.getWeightInGrams());
        }

        // 2. DailyLog
        LocalDate today = LocalDate.now();
        DailyLog log = dailyLogRepository.findByUserAndLogDate(user, today)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(user);
                    newLog.setLogDate(today);
                    return dailyLogRepository.save(newLog);
                });

        // 3. Готовим Per100g + имя + ссылку
        RecipeNutritionService.Per100g per100g;
        String itemName;
        Recipe recipe = null;
        Ingredient ingredient = null;

        if (hasRecipe) {
            recipe = recipeRepository.findById(portion.getRecipeId())
                    .orElseThrow(() -> new RecipeNotFoundException(
                            "Рецепт с ID " + portion.getRecipeId() + " не найден"));
            per100g = recipeNutritionService.calculatePer100g(recipe);
            itemName = recipe.getName();
        } else {
            ingredient = ingredientRepository.findById(portion.getIngredientId())
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Ингредиент с ID " + portion.getIngredientId() + " не найден"));
            per100g = recipeNutritionService.per100gFromIngredient(ingredient);
            itemName = ingredient.getName();
        }

        // 4. Расчёт на порцию
        RecipeNutritionService.Per100g forPortion =
                recipeNutritionService.calculateForPortion(per100g, portion.getWeightInGrams());

        // 5. FoodEntry
        FoodEntry entry = new FoodEntry();
        entry.setDailyLog(log);
        entry.setRecipe(recipe);              // null для ингредиента
        entry.setIngredient(ingredient);      // null для рецепта
        entry.setItemName(itemName);
        entry.setWeightInGrams(portion.getWeightInGrams());

        entry.setCaloriesPer100g(per100g.calories());
        entry.setProteinPer100g(per100g.protein());
        entry.setFatPer100g(per100g.fat());
        entry.setCarbsPer100g(per100g.carbs());

        entry.setCalories(forPortion.calories());
        entry.setProtein(forPortion.protein());
        entry.setFat(forPortion.fat());
        entry.setCarbs(forPortion.carbs());

        foodEntryRepository.save(entry);

        recalculateDailyTotals(log);

        return calculateStatsForDate(today, user);
    }

    @Transactional(readOnly = true)
    public DailyStatsDto getTodayStats(User user) {
        return calculateStatsForDate(LocalDate.now(), user);
    }

    @Transactional(readOnly = true)
    public DailyStatsDto getStatsByDate(LocalDate date, User user) {
        return calculateStatsForDate(date, user);
    }

    /**
     * Получить страницу логов по пользователю (для списка статистики).
     */
    public Page<DailyLog> getStatsPage(User user, int page, int size) {
        return dailyLogRepository.findByUser(user, PageRequest.of(page, size));
    }

    @Transactional
    private void recalculateDailyTotals(DailyLog log) {
        List<FoodEntry> entries = foodEntryRepository.findByDailyLogId(log.getId());

        BigDecimal totalCalories = entries.stream()
                .map(FoodEntry::getCalories)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalProtein = entries.stream()
                .map(FoodEntry::getProtein)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalFat = entries.stream()
                .map(FoodEntry::getFat)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal totalCarbs = entries.stream()
                .map(FoodEntry::getCarbs)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        log.setCalories(totalCalories);
        log.setProtein(totalProtein);
        log.setFat(totalFat);
        log.setCarbs(totalCarbs);

        dailyLogRepository.save(log);
    }

    private DailyStatsDto calculateStatsForDate(LocalDate date, User user) {
        DailyLog dailyLog = dailyLogRepository.findByUserAndLogDate(user, date)
                .orElseGet(() -> {
                    DailyLog empty = new DailyLog();
                    empty.setUser(user);
                    empty.setLogDate(date);
                    return empty;
                });

        Double targetCalories = null;
        Double caloriesProgressPercent = null;
        try {
            BigDecimal calculated = nutritionCalculationService.calculateDailyCaloriesForUser(user);
            if (calculated != null && calculated.compareTo(BigDecimal.ZERO) > 0) {
                targetCalories = calculated.doubleValue();
                caloriesProgressPercent = (dailyLog.getCalories().doubleValue() / targetCalories) * 100.0;
            }
        } catch (Exception e) {
            log.warn("Не удалось рассчитать цель по калориям для {}: {}", user.getUsername(), e.getMessage());
        }

        TargetProteinResponse proteinResp = nutritionCalculationService.calculateTargetProteinFromGoals(user);
        Double targetProtein = null;
        Double proteinProgressPercent = null;
        if (proteinResp != null && proteinResp.targetProteinGramsPerDay() > 0) {
            targetProtein = proteinResp.targetProteinGramsPerDay();
            proteinProgressPercent = (dailyLog.getProtein().doubleValue() / targetProtein) * 100.0;
        }

        List<FoodEntryDto> entries = Collections.emptyList();
        if (dailyLog.getId() != null) {
            entries = foodEntryRepository.findByDailyLogId(dailyLog.getId()).stream()
                    .map(FoodEntryDto::fromEntity)
                    .toList();
        }

        return new DailyStatsDto(
                dailyLog.getCalories().doubleValue(),
                dailyLog.getProtein().doubleValue(),
                dailyLog.getFat().doubleValue(),
                dailyLog.getCarbs().doubleValue(),
                targetCalories,
                caloriesProgressPercent,
                targetProtein,
                proteinProgressPercent,
                entries
        );
    }
    @Transactional
    public void deleteEntry(Long entryId, User user) {
        FoodEntry entry = foodEntryRepository.findById(entryId)
                .orElseThrow(() -> new IllegalArgumentException("Запись не найдена: " + entryId));

        // Проверка: запись принадлежит текущему пользователю
        if (!entry.getDailyLog().getUser().getId().equals(user.getId())) {
            throw new IllegalArgumentException("Нет доступа к записи");
        }

        DailyLog log = entry.getDailyLog();
        foodEntryRepository.delete(entry);
        recalculateDailyTotals(log);
    }


    @Transactional
    public int addFromPlan(AddFromPlanRequest request, User user) {
        LocalDate date = request.getDate();
        DailyLog log = dailyLogRepository.findByUserAndLogDate(user, date)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(user);
                    newLog.setLogDate(date);
                    return dailyLogRepository.save(newLog);
                });

        List<MealPlanItem> items = mealPlanItemRepository.findAllById(request.getItemIds());
        int added = 0;

        for (MealPlanItem item : items) {
            // Проверка, что item принадлежит плану пользователя
            if (!item.getPlan().getId().equals(request.getPlanId())) continue;
            if (!item.getPlan().getUser().getId().equals(user.getId())) continue;

            FoodEntry entry = new FoodEntry();
            entry.setDailyLog(log);
            entry.setWeightInGrams(0);

            if (item.getRecipe() != null) {
                Recipe recipe = item.getRecipe();
                double servings = item.getServings() != null ? item.getServings() : 1.0;
                double recipeTotalWeight = recipe.getTotalWeight();
                double recipeServings = recipe.getServings() != null ? recipe.getServings() : 1;
                double portionWeight = (recipeTotalWeight / recipeServings) * servings;

                RecipeNutritionService.Per100g per100g = recipeNutritionService.calculatePer100g(recipe);
                RecipeNutritionService.Per100g forPortion = recipeNutritionService.calculateForPortion(per100g, portionWeight);

                entry.setRecipe(recipe);
                entry.setItemName(recipe.getName());
                entry.setWeightInGrams(portionWeight);
                entry.setCaloriesPer100g(per100g.calories());
                entry.setProteinPer100g(per100g.protein());
                entry.setFatPer100g(per100g.fat());
                entry.setCarbsPer100g(per100g.carbs());
                entry.setCalories(forPortion.calories());
                entry.setProtein(forPortion.protein());
                entry.setFat(forPortion.fat());
                entry.setCarbs(forPortion.carbs());
            } else if (item.getIngredient() != null) {
                Ingredient ing = item.getIngredient();
                double weight = item.getWeightInGrams() != null ? item.getWeightInGrams() : 100.0;
                double factor = weight / 100.0;

                double calsPer100 = ing.calculateCaloriesPer100g();
                double protPer100 = ing.getProteinsPer100g() != null ? ing.getProteinsPer100g() : 0;
                double fatPer100 = ing.getFatsPer100g() != null ? ing.getFatsPer100g() : 0;
                double carbsPer100 = ing.getCarbsPer100g() != null ? ing.getCarbsPer100g() : 0;

                entry.setRecipe(null);
                entry.setItemName(ing.getName());
                entry.setWeightInGrams(weight);
                entry.setCaloriesPer100g(BigDecimal.valueOf(calsPer100));
                entry.setProteinPer100g(BigDecimal.valueOf(protPer100));
                entry.setFatPer100g(BigDecimal.valueOf(fatPer100));
                entry.setCarbsPer100g(BigDecimal.valueOf(carbsPer100));
                entry.setCalories(BigDecimal.valueOf(calsPer100 * factor));
                entry.setProtein(BigDecimal.valueOf(protPer100 * factor));
                entry.setFat(BigDecimal.valueOf(fatPer100 * factor));
                entry.setCarbs(BigDecimal.valueOf(carbsPer100 * factor));
            } else {
                continue;
            }

            foodEntryRepository.save(entry);
            added++;
        }

        recalculateDailyTotals(log);
        return added;
    }
}


