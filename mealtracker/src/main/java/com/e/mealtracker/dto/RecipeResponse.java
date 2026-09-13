
package com.e.mealtracker.dto;

import com.e.mealtracker.domain.RecipeVisibility;
import java.math.BigDecimal;

public record RecipeResponse(
        Long id,
        String name,
        String category,
        BigDecimal totalCalories,
        BigDecimal totalProteins,
        BigDecimal totalFats,
        BigDecimal totalCarbs,
        RecipeVisibility visibility
) {
    public static RecipeResponse fromRecipe(com.e.mealtracker.domain.Recipe recipe) {
        return new RecipeResponse(
                recipe.getId(),
                recipe.getName(),
                recipe.getCategory() != null ? recipe.getCategory().getDisplayName() : null,
                recipe.getTotalCalories(),
                recipe.getTotalProteins(),
                recipe.getTotalFats(),
                recipe.getTotalCarbs(),
                recipe.getVisibility()
        );
    }
}


