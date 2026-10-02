package com.e.mealtracker.dto;

import com.e.mealtracker.domain.MealPlanItem;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MealPlanItemDto {
    private Long id;
    private LocalDate planDate;
    private String mealType;
    private Long recipeId;
    private String recipeName;
    private Long ingredientId;
    private String ingredientName;
    private Double servings;
    private Double weightInGrams;
    private String customName;

    public static MealPlanItemDto fromEntity(MealPlanItem item) {
        MealPlanItemDto dto = new MealPlanItemDto();
        dto.setId(item.getId());
        dto.setPlanDate(item.getPlanDate());
        dto.setMealType(item.getMealType() != null ? item.getMealType().name() : null);
        if (item.getRecipe() != null) {
            dto.setRecipeId(item.getRecipe().getId());
            dto.setRecipeName(item.getRecipe().getName());
        }
        if (item.getIngredient() != null) {
            dto.setIngredientId(item.getIngredient().getId());
            dto.setIngredientName(item.getIngredient().getName());
        }
        dto.setServings(item.getServings());
        dto.setWeightInGrams(item.getWeightInGrams());
        dto.setCustomName(item.getCustomName());
        return dto;
    }
}