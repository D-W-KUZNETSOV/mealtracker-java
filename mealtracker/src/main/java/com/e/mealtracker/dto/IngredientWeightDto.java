package com.e.mealtracker.dto;


import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class IngredientWeightDto {
    @NotNull(message = "Название ингредиента обязательно")
    private String ingredientName;

    // Вес должен быть больше 0 и не меньше 1 грамма (можно поменять на 0.1, если нужны доли)
    @Min(value = 1, message = "Вес ингредиента должен быть не менее 1 грамма")
    private double weightInGrams;
}
