package com.e.mealtracker.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class IngredientWeightDto {
    private String ingredientName;

    @DecimalMin(value = "0.1", message = "Вес ингредиента должен быть не менее 0.1 грамма")
    private double weightInGrams;
}
