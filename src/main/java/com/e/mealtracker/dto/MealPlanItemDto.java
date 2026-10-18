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
    // 🆕 КБЖУ для элемента плана
    private Double calories;
    private Double proteins;
    private Double fats;
    private Double carbs;

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

        // 🆕 Расчёт КБЖУ
        if (item.getRecipe() != null) {
            // 1. Вес одной порции рецепта
            double recipeTotalWeight = item.getRecipe().getTotalWeight();
            int recipeServings = item.getRecipe().getServings() != null && item.getRecipe().getServings() > 0
                    ? item.getRecipe().getServings() : 1;
            double oneServingWeight = recipeTotalWeight / recipeServings;

            // 2. Вес того, что в плане
            double planServings = item.getServings() != null ? item.getServings() : 1.0;
            double weight = item.getWeightInGrams() != null
                    ? item.getWeightInGrams()
                    : oneServingWeight * planServings;

            // 3. Считаем КБЖУ по весу
            double ratio = weight / 100.0;
            double calPer100 = item.getRecipe().getCaloriesPer100g() != null
                    ? item.getRecipe().getCaloriesPer100g().doubleValue() : 0.0;
            double protPer100 = item.getRecipe().getProteinPer100g() != null
                    ? item.getRecipe().getProteinPer100g().doubleValue() : 0.0;
            double fatPer100 = item.getRecipe().getFatPer100g() != null
                    ? item.getRecipe().getFatPer100g().doubleValue() : 0.0;
            double carbPer100 = item.getRecipe().getCarbsPer100g() != null
                    ? item.getRecipe().getCarbsPer100g().doubleValue() : 0.0;

            dto.setCalories(calPer100 * ratio);
            dto.setProteins(protPer100 * ratio);
            dto.setFats(fatPer100 * ratio);
            dto.setCarbs(carbPer100 * ratio);

            // Записываем вес в DTO, чтобы фронт видел граммы
            dto.setWeightInGrams(weight);
        } else if (item.getIngredient() != null) {
            // Для ингредиента (если он добавлен напрямую)
            double weight = item.getWeightInGrams() != null ? item.getWeightInGrams() : 0.0;
            double ratio = weight / 100.0;

            double calPer100 = item.getIngredient().calculateCaloriesPer100g();
            double protPer100 = item.getIngredient().getProteinsPer100g() != null
                    ? item.getIngredient().getProteinsPer100g() : 0.0;
            double fatPer100 = item.getIngredient().getFatsPer100g() != null
                    ? item.getIngredient().getFatsPer100g() : 0.0;
            double carbPer100 = item.getIngredient().getCarbsPer100g() != null
                    ? item.getIngredient().getCarbsPer100g() : 0.0;

            dto.setCalories(calPer100 * ratio);
            dto.setProteins(protPer100 * ratio);
            dto.setFats(fatPer100 * ratio);
            dto.setCarbs(carbPer100 * ratio);
        }

        return dto;
    }
}