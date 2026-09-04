package com.e.mealtracker.service;

import com.e.mealtracker.domain.*;
import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.exception.InvalidPortionWeightException;
import com.e.mealtracker.exception.RecipeNotFoundException;
import com.e.mealtracker.repository.DailyLogRepository;
import com.e.mealtracker.repository.RecipeRepository;
import com.e.mealtracker.repository.UserGoalsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final RecipeRepository recipeRepository;
    private final DailyLogRepository dailyLogRepository;
    private final UserGoalsRepository userGoalsRepository;

    @Transactional
    public DailyStatsDto addPortionAndReturnTodayStats(RecipePortionRequest portion) {
        // Проверка веса — теперь с кастомным исключением
        if (portion.getWeightInGrams() <= 0) {
            throw new InvalidPortionWeightException(portion.getWeightInGrams());
        }

        Recipe recipe = recipeRepository.findById(portion.getRecipeId())
                .orElseThrow(() -> new RecipeNotFoundException(portion.getRecipeId()));

        DailyLog log = new DailyLog();
        log.setRecipe(recipe);
        log.setWeightInGrams(portion.getWeightInGrams());
        log.setDate(LocalDate.now());

        dailyLogRepository.save(log);

        return calculateStatsForDate(LocalDate.now());
    }


    public DailyStatsDto getTodayStats() {
        return calculateStatsForDate(LocalDate.now());
    }

    public DailyStatsDto getStatsByDate(LocalDate date) {
        return calculateStatsForDate(date);
    }

    private DailyStatsDto calculateStatsForDate(LocalDate date) {
        List<DailyLog> logs = dailyLogRepository.findByDate(date);

        double totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;

        if (!logs.isEmpty()) {
            for (DailyLog log : logs) {
                Recipe recipe = log.getRecipe();
                double portionWeight = log.getWeightInGrams();

                double totalRecipeWeight = recipe.getIngredients().stream()
                        .mapToDouble(RecipeIngredient::getWeightInGrams)
                        .sum();

                if (totalRecipeWeight == 0) {
                    throw new IllegalArgumentException(
                            "Общий вес ингредиентов рецепта равен 0. Проверьте веса в рецепте.");
                }

                for (RecipeIngredient ri : recipe.getIngredients()) {
                    Ingredient ing = ri.getIngredient();

                    if (ing.getCaloriesPer100g() == null
                            || ing.getProteinsPer100g() == null
                            || ing.getFatsPer100g() == null
                            || ing.getCarbsPer100g() == null) {
                        throw new IllegalArgumentException(
                                "У ингредиента '" + ing.getName() + "' неполные данные КБЖУ.");
                    }

                    double weightInPortion = (ri.getWeightInGrams() / totalRecipeWeight) * portionWeight;

                    totalCalories += ing.getCaloriesPer100g() * (weightInPortion / 100.0);
                    totalProteins += ing.getProteinsPer100g() * (weightInPortion / 100.0);
                    totalFats += ing.getFatsPer100g() * (weightInPortion / 100.0);
                    totalCarbs += ing.getCarbsPer100g() * (weightInPortion / 100.0);
                }
            }
        }

        // Получаем последнюю сохранённую цель пользователя
        Optional<UserGoals> goalsOpt = userGoalsRepository.findFirstByOrderByCreatedAtDesc();
        Double targetProtein = null;
        Double proteinProgressPercent = null;

        if (goalsOpt.isPresent()) {
            UserGoals goals = goalsOpt.get();
            if (goals.getCurrentWeightKg() > 0 && goals.getProteinPerKg() > 0) {
                targetProtein = goals.getCurrentWeightKg() * goals.getProteinPerKg();

                if (targetProtein > 0) {
                    proteinProgressPercent = Math.round(
                            (totalProteins / targetProtein) * 1000.0
                    ) / 10.0;
                }
            }
        }

        return new DailyStatsDto(
                totalCalories,
                totalProteins,
                totalFats,
                totalCarbs,
                targetProtein,
                proteinProgressPercent
        );
    }
}


