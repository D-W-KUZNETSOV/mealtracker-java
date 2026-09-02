package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class IngredientResponseDto {
    private Long id;
    private String name;
    private double fatsPer100g;
    private double proteinsPer100g;
    private double carbsPer100g;
    private Double caloriesPer100g;
    // totalCaloriesPer100g можно добавить сразу здесь
}
