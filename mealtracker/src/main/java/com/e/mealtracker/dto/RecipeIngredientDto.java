package com.e.mealtracker.dto;

import com.e.mealtracker.domain.RecipeIngredient;
import lombok.Data;

@Data
public class RecipeIngredientDto {
    private Long ingredientId;
    private String ingredientName;
    private double weightInGrams;
    private double calories; // вес * ккал/100 / 100

    public static RecipeIngredientDto fromEntity(RecipeIngredient ri) {
        RecipeIngredientDto dto = new RecipeIngredientDto();
        var ing = ri.getIngredient();
        dto.setIngredientId(ing != null ? ing.getId() : null);
        dto.setIngredientName(ing != null ? ing.getName() : "Неизвестный ингредиент");
        dto.setWeightInGrams(ri.getWeightInGrams());

        double calsPer100 = ing != null ? ing.calculateCaloriesPer100g() : 0.0;
        dto.setCalories((calsPer100 * ri.getWeightInGrams()) / 100.0);

        return dto;
    }
}

