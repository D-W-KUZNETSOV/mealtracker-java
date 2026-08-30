package com.e.mealtracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecipeSummaryDto {
    private String name;
    private List<IngredientItemDto> ingredients;
    private double totalCalories;
    private double totalFats;
    private double totalProteins;
    private double totalCarbs;

    @Data
    public static class IngredientItemDto {
        private String name;
        private double quantityGrams;      // сколько грамм ингредиента в рецепте
        private double caloriesPer100g;
        private double fatsPer100g;
        private double proteinsPer100g;
        private double carbsPer100g;
        private double itemCalories;     // калории именно для этой порции
    }
}

