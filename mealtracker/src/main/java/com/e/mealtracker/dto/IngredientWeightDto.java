package com.e.mealtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngredientWeightDto {
    @NotNull(message = "ID ингредиента обязателен")
    private Long ingredientId;

    @Min(value = 1, message = "Вес ингредиента должен быть не менее 1 грамма")
    private double weightInGrams;
}
