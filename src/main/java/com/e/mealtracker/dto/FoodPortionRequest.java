package com.e.mealtracker.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Запрос на добавление порции в дневник.
 * Ровно одно из полей recipeId / ingredientId должно быть заполнено.
 * weightInGrams — вес порции в граммах (обязательно).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FoodPortionRequest {
    private Long recipeId;       // nullable — если это рецепт
    private Long ingredientId;   // nullable — если это ингредиент (продукт)
    private double weightInGrams;
}