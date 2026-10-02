package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.FoodEntryDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.dto.TargetProteinResponse;
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


    /**
     * Добавить порцию и вернуть статистику за сегодня.
     * @param portion данные о порции
     * @param user текущий пользователь (должен быть уже аутентифицирован)
     */
    @Transactional
    public DailyStatsDto addPortionAndReturnTodayStats(RecipePortionRequest portion, User user) {
        LocalDate today = LocalDate.now();

        DailyLog log = dailyLogRepository.findByUserAndLogDate(user, today)
                .orElseGet(() -> {
                    DailyLog newLog = new DailyLog();
                    newLog.setUser(user);
                    newLog.setLogDate(today);
                    return dailyLogRepository.save(newLog);
                });

        Recipe recipe = recipeRepository.findById(portion.getRecipeId())
                .orElseThrow(() -> new RecipeNotFoundException("Рецепт с ID " + portion.getRecipeId() + " не найден"));

        if (portion.getWeightInGrams() <= 0) {
            throw new InvalidPortionWeightException(portion.getWeightInGrams());
        }

        // Считаем per-100g из totalCalories и общего веса ингредиентов
        RecipeNutritionService.Per100g per100g =
                recipeNutritionService.calculatePer100g(recipe);

        RecipeNutritionService.Per100g forPortion =
                recipeNutritionService.calculateForPortion(per100g, portion.getWeightInGrams());

        BigDecimal caloriesPer100g = per100g.calories();
        BigDecimal proteinPer100g  = per100g.protein();
        BigDecimal fatPer100g      = per100g.fat();
        BigDecimal carbsPer100g    = per100g.carbs();

        BigDecimal calories = forPortion.calories();
        BigDecimal protein  = forPortion.protein();
        BigDecimal fat      = forPortion.fat();
        BigDecimal carbs    = forPortion.carbs();

        FoodEntry entry = new FoodEntry();
        entry.setDailyLog(log);
        entry.setRecipe(recipe);
        entry.setRecipeName(recipe.getName());
        entry.setWeightInGrams(portion.getWeightInGrams());

        entry.setCaloriesPer100g(caloriesPer100g);
        entry.setProteinPer100g(proteinPer100g);
        entry.setFatPer100g(fatPer100g);
        entry.setCarbsPer100g(carbsPer100g);

        entry.setCalories(calories);
        entry.setProtein(protein);
        entry.setFat(fat);
        entry.setCarbs(carbs);

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
}


