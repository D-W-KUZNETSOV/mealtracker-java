package com.e.mealtracker.dto;

import com.e.mealtracker.domain.RecipeVisibility;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;

@Data
public class RecipeSummaryDto {
    private Long id;                    // 🆕
    private String name;
    private String description;
    private String imageUrl;
    private RecipeVisibility visibility;
    private List<IngredientItemDto> ingredients;
    private BigDecimal totalCalories;
    private BigDecimal totalFats;
    private BigDecimal totalProteins;
    private BigDecimal totalCarbs;
    private Double totalWeight;
    private String category;   // 🆕
    private Integer servings;
    private Double servingSizeGrams;
    private List<String> steps;   // 🆕

    @Data
    public static class IngredientItemDto {
        private String name;
        private double quantityGrams;
        private double caloriesPer100g;
        private double fatsPer100g;
        private double proteinsPer100g;
        private double carbsPer100g;
        private double itemCalories;
        private Long ingredientId;   // 🆕
    }
}
