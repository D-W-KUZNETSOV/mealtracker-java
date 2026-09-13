package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.exception.InvalidPortionWeightException;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final DailyLogRepository dailyLogRepository;
    private final FoodEntryRepository foodEntryRepository;
    private final RecipeRepository recipeRepository;
    private final UserRepository userRepository;
    private final UserGoalsRepository userGoalsRepository;

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
        BigDecimal totalWeight = BigDecimal.valueOf(recipe.getIngredients().stream()
                .mapToDouble(ri -> ri.getWeightInGrams())
                .sum());

        BigDecimal caloriesPer100g;
        BigDecimal proteinPer100g;
        BigDecimal fatPer100g;
        BigDecimal carbsPer100g;

        if (totalWeight.compareTo(BigDecimal.ZERO) > 0) {
            caloriesPer100g = recipe.getTotalCalories()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalWeight, MathContext.DECIMAL32);
            proteinPer100g = recipe.getTotalProteins()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalWeight, MathContext.DECIMAL32);
            fatPer100g = recipe.getTotalFats()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalWeight, MathContext.DECIMAL32);
            carbsPer100g = recipe.getTotalCarbs()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(totalWeight, MathContext.DECIMAL32);
        } else {
            // Если веса ингредиентов нет (редкий кейс), можно поставить 0 или кинуть исключение
            caloriesPer100g = BigDecimal.ZERO;
            proteinPer100g = BigDecimal.ZERO;
            fatPer100g = BigDecimal.ZERO;
            carbsPer100g = BigDecimal.ZERO;
        }

        BigDecimal weight = BigDecimal.valueOf(portion.getWeightInGrams());
        BigDecimal factor = weight.divide(BigDecimal.valueOf(100), MathContext.DECIMAL32);

        BigDecimal calories = caloriesPer100g.multiply(factor);
        BigDecimal protein = proteinPer100g.multiply(factor);
        BigDecimal fat = fatPer100g.multiply(factor);
        BigDecimal carbs = carbsPer100g.multiply(factor);

        FoodEntry entry = new FoodEntry();
        entry.setDailyLog(log);
        entry.setRecipe(recipe);
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


    public DailyStatsDto getTodayStats(User user) {
        return calculateStatsForDate(LocalDate.now(), user);
    }

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
        DailyLog log = dailyLogRepository.findByUserAndLogDate(user, date)
                .orElseGet(() -> {
                    DailyLog empty = new DailyLog();
                    empty.setUser(user);
                    empty.setLogDate(date);
                    // Можно сохранить пустой лог, если хочешь, или оставить transient:
                    // return dailyLogRepository.save(empty);
                    return empty;
                });

        Optional<UserGoals> goalsOpt = userGoalsRepository.findFirstByUserOrderByCreatedAtDesc(user);

        Double targetProtein = null;
        Double proteinProgressPercent = null;

        if (goalsOpt.isPresent()) {
            UserGoals goals = goalsOpt.get();
            if (goals.getCurrentWeightKg() > 0 && goals.getProteinPerKg() > 0) {
                targetProtein = goals.getCurrentWeightKg() * goals.getProteinPerKg();
                if (targetProtein > 0) {
                    proteinProgressPercent = (log.getProtein().doubleValue() / targetProtein) * 100.0;
                }
            }
        }

        return new DailyStatsDto(
                log.getCalories().doubleValue(),
                log.getProtein().doubleValue(),
                log.getFat().doubleValue(),
                log.getCarbs().doubleValue(),
                targetProtein,
                proteinProgressPercent
        );
    }
}


