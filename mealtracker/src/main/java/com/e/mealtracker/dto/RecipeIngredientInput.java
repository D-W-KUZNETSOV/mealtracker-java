package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class RecipeIngredientInput {
    private String name;           // имя ингредиента (должно совпадать с таблицей ingredients.name)
    private double quantityGrams;   // сколько грамм этого ингредиента в рецепте
}
