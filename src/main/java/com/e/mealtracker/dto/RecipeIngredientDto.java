package com.e.mealtracker.dto;

import com.e.mealtracker.domain.RecipeIngredient;
import lombok.Data;

@Data
public class RecipeIngredientDto {
    private Long ingredientId;
    private String ingredientName;
    private double weightInGrams;
    private double calories;
    private double proteins;
    private double fats;
    private double carbs;

    public static RecipeIngredientDto fromEntity(RecipeIngredient ri) {
        RecipeIngredientDto dto = new RecipeIngredientDto();
        var ing = ri.getIngredient();

        dto.setIngredientId(ing != null ? ing.getId() : null);
        dto.setIngredientName(ing != null ? ing.getName() : "Неизвестный ингредиент");
        dto.setWeightInGrams(ri.getWeightInGrams());

        double ratio = ri.getWeightInGrams() / 100.0;

        if (ing != null) {
            // Было: ing.getCaloriesPer100g() * ratio
            // Стало:
            dto.setCalories(ing.calculateCaloriesPer100g() * ratio);

            // Для БЖУ оставляем как есть — у них есть геттеры
            dto.setProteins(
                    (ing.getProteinsPer100g() != null ? ing.getProteinsPer100g() : 0.0) * ratio
            );
            dto.setFats(
                    (ing.getFatsPer100g() != null ? ing.getFatsPer100g() : 0.0) * ratio
            );
            dto.setCarbs(
                    (ing.getCarbsPer100g() != null ? ing.getCarbsPer100g() : 0.0) * ratio
            );
        } else {
            dto.setCalories(0.0);
            dto.setProteins(0.0);
            dto.setFats(0.0);
            dto.setCarbs(0.0);
        }

        return dto;
    }
}


