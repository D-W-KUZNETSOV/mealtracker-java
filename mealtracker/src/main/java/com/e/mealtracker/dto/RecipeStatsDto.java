package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class RecipeStatsDto {
    private String recipeName;
    private double totalCalories;
    private double totalProteins;
    private double totalFats;
    private double totalCarbs;

    // Детализация по ингредиентам (опционально, но очень полезно для трекера)
    private java.util.List<IngredientStats> ingredientsStats;

    @Data
    public static class IngredientStats {
        private String ingredientName;
        private double weightInGrams;
        private double calories;
        private double proteins;
        private double fats;
        private double carbs;
    }
}

