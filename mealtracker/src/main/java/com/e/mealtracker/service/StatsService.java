package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.repository.RecipeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;


import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatsService {

    private final RecipeRepository recipeRepository;

    /**
     * Считает суммарные КБЖУ по списку пар (recipeId, weightInGrams).
     * Вес — это сколько граммов этого рецепта ты реально съел.
     */
    public DailyStatsDto calculateStats(List<RecipePortionRequest> portions) {
        double totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;

        // загружаем все рецепты одним запросом
        List<Long> ids = portions.stream().map(RecipePortionRequest::getRecipeId).toList();
        Map<Long, Recipe> recipeMap = recipeRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Recipe::getId, r -> r));

        for (RecipePortionRequest p : portions) {
            Recipe recipe = recipeMap.get(p.getRecipeId());
            if (recipe == null) {
                throw new IllegalArgumentException("Рецепт с ID " + p.getRecipeId() + " не найден");
            }

            // коэффициент пересчёта: сколько «сотен грамм» в твоей порции
            double factor = p.getWeightInGrams() / 100.0;

            // считаем КБЖУ рецепта на 100 г
            double recipeCaloriesPer100 = 0;
            double recipeProteinsPer100 = 0;
            double recipeFatsPer100 = 0;
            double recipeCarbsPer100 = 0;

            for (RecipeIngredient ri : recipe.getIngredients()) {
                Ingredient ing = ri.getIngredient();
                double ingredientFactor = ri.getWeightInGrams() / 100.0; // вес ингредиента в рецепте

                recipeCaloriesPer100 += ing.getCaloriesPer100g() * ingredientFactor;
                recipeProteinsPer100 += ing.getProteinsPer100g() * ingredientFactor;
                recipeFatsPer100 += ing.getFatsPer100g() * ingredientFactor;
                recipeCarbsPer100 += ing.getCarbsPer100g() * ingredientFactor;
            }

            // теперь умножаем на твою порцию
            totalCalories += recipeCaloriesPer100 * factor;
            totalProteins += recipeProteinsPer100 * factor;
            totalFats += recipeFatsPer100 * factor;
            totalCarbs += recipeCarbsPer100 * factor;
        }

        return new DailyStatsDto(totalCalories, totalProteins, totalFats, totalCarbs);
    }
}
