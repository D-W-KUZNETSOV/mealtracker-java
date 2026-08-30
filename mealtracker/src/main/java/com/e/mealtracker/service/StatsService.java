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
     * Считает суммарные КБЖУ по списку порций.
     * Вес в RecipePortionRequest — это сколько граммов блюда реально съедено.
     */
    public DailyStatsDto calculateStats(List<RecipePortionRequest> portions) {
        double totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;

        if (portions == null || portions.isEmpty()) {
            return new DailyStatsDto(0, 0, 0, 0);
        }

        List<Long> ids = portions.stream()
                .map(RecipePortionRequest::getRecipeId)
                .toList();

        Map<Long, Recipe> recipeMap = recipeRepository.findAllById(ids).stream()
                .collect(Collectors.toMap(Recipe::getId, r -> r));

        for (RecipePortionRequest p : portions) {
            Recipe recipe = recipeMap.get(p.getRecipeId());
            if (recipe == null) {
                throw new IllegalArgumentException("Рецепт с ID " + p.getRecipeId() + " не найден");
            }

            double portionWeight = p.getWeightInGrams();
            if (portionWeight <= 0) {
                throw new IllegalArgumentException("Вес порции должен быть больше 0");
            }

            // Считаем общий вес всех ингредиентов в полном рецепте
            double totalRecipeWeight = recipe.getIngredients().stream()
                    .mapToDouble(ri -> ri.getWeightInGrams())
                    .sum();

            if (totalRecipeWeight == 0) {
                throw new IllegalArgumentException(
                        "Общий вес ингредиентов рецепта равен 0. Проверьте веса в рецепте."
                );
            }

            for (RecipeIngredient ri : recipe.getIngredients()) {
                Ingredient ing = ri.getIngredient();

                if (ing.getCaloriesPer100g() == null
                        || ing.getProteinsPer100g() == null
                        || ing.getFatsPer100g() == null
                        || ing.getCarbsPer100g() == null) {
                    throw new IllegalArgumentException(
                            "У ингредиента '" + ing.getName() + "' неполные данные КБЖУ."
                    );
                }

                // Пропорция: сколько граммов этого ингредиента в твоей порции
                double weightInPortion = (ri.getWeightInGrams() / totalRecipeWeight) * portionWeight;

                // КБЖУ на 100 г × (вес в порции / 100)
                totalCalories += ing.getCaloriesPer100g() * (weightInPortion / 100.0);
                totalProteins  += ing.getProteinsPer100g()  * (weightInPortion / 100.0);
                totalFats       += ing.getFatsPer100g()      * (weightInPortion / 100.0);
                totalCarbs       += ing.getCarbsPer100g()    * (weightInPortion / 100.0);
            }
        }

        return new DailyStatsDto(totalCalories, totalProteins, totalFats, totalCarbs);
    }


}
