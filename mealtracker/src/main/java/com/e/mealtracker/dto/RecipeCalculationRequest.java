package com.e.mealtracker.dto;

import lombok.Data;
import java.util.List;

@Data
public class RecipeCalculationRequest {
    private String name;
    private List<RecipeIngredientInput> ingredients;
}
