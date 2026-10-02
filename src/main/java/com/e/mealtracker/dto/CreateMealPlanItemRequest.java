package com.e.mealtracker.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class CreateMealPlanItemRequest {
    private LocalDate planDate;
    private String mealType;
    private Long recipeId;
    private Long ingredientId;
    private Double servings;
    private Double weightInGrams;
    private String customName;
}
