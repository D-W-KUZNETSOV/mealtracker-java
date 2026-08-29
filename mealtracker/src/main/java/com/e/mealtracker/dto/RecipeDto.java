package com.e.mealtracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecipeDto {
    private String name;
    private String category;
    private List<IngredientWeightDto> ingredients;
    private double totalCalories;
}

