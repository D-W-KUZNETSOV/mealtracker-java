package com.e.mealtracker.dto;

import lombok.Data;
import jakarta.validation.constraints.Positive;

@Data
public class RecipeIngredientInput {
    private Long ingredientId;

    @Positive(message = "Вес должен быть больше 0")
    private double quantityGrams;
}
