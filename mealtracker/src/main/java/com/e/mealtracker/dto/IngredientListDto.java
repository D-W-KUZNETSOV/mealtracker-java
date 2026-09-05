package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class IngredientListDto {
    private Long id;
    private String name;
    private Double fatsPer100g;
    private Double proteinsPer100g;
    private Double carbsPer100g;
    private Double caloriesPer100g; // можно брать из calculateCaloriesPer100g
}

