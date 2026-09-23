package com.e.mealtracker.dto;

import com.e.mealtracker.domain.Ingredient;
import lombok.Data;

@Data
public class IngredientResponseDto {
    private Long id;
    private String name;
    private double fatsPer100g;
    private double proteinsPer100g;
    private double carbsPer100g;
    private double caloriesPer100g; // вот так правильно: это данные, а не метод

    public static IngredientResponseDto fromEntity(Ingredient ingredient) {
        IngredientResponseDto dto = new IngredientResponseDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        // безопасно: если null, будет 0.0
        dto.setFatsPer100g(ingredient.getFatsPer100g() != null ? ingredient.getFatsPer100g() : 0.0);
        dto.setProteinsPer100g(ingredient.getProteinsPer100g() != null ? ingredient.getProteinsPer100g() : 0.0);
        dto.setCarbsPer100g(ingredient.getCarbsPer100g() != null ? ingredient.getCarbsPer100g() : 0.0);

        // считаем калории прямо здесь
        dto.setCaloriesPer100g(ingredient.calculateCaloriesPer100g());

        return dto;
    }
}

