package com.e.mealtracker.service;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.domain.Recipe;
import com.e.mealtracker.domain.RecipeIngredient;
import com.e.mealtracker.dto.RecipeStatsDto;
import com.e.mealtracker.repository.RecipeIngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeStatsService {

    private final RecipeIngredientRepository recipeIngredientRepo;

    public RecipeStatsDto calculateStatsForRecipe(Long recipeId) {
        // Получаем все связи «рецепт–ингредиент» для этого рецепта
        List<RecipeIngredient> links = recipeIngredientRepo.findAllByRecipeId(recipeId);

        double totalCalories = 0;
        double totalProteins = 0;
        double totalFats = 0;
        double totalCarbs = 0;

        List<RecipeStatsDto.IngredientStats> ingredientStatsList = new ArrayList<>();

        for (RecipeIngredient link : links) {
            Ingredient ing = link.getIngredient();
            double weightInGrams = link.getWeightInGrams();

            // КБЖУ на 100 г
            double calsPer100 = ing.calculateCaloriesPer100g();
            double proPer100 = ing.getProteinsPer100g();
            double fatPer100 = ing.getFatsPer100g();
            double carbPer100 = ing.getCarbsPer100g();

            // Пересчёт на вес в рецепте
            double multiplier = weightInGrams / 100.0;
            double calories = calsPer100 * multiplier;
            double proteins = proPer100 * multiplier;
            double fats = fatPer100 * multiplier;
            double carbs = carbPer100 * multiplier;

            totalCalories += calories;
            totalProteins += proteins;
            totalFats += fats;
            totalCarbs += carbs;

            RecipeStatsDto.IngredientStats item = new RecipeStatsDto.IngredientStats();
            item.setIngredientName(ing.getName());
            item.setWeightInGrams(weightInGrams);
            item.setCalories(calories);
            item.setProteins(proteins);
            item.setFats(fats);
            item.setCarbs(carbs);
            ingredientStatsList.add(item);
        }

        RecipeStatsDto dto = new RecipeStatsDto();
        dto.setRecipeName("Рецепт ID " + recipeId); // Лучше брать имя из Recipe, если есть доступ к нему
        dto.setTotalCalories(totalCalories);
        dto.setTotalProteins(totalProteins);
        dto.setTotalFats(totalFats);
        dto.setTotalCarbs(totalCarbs);
        dto.setIngredientsStats(ingredientStatsList);

        return dto;
    }
}
