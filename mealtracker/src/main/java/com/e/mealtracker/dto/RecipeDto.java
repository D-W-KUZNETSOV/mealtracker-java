package com.e.mealtracker.dto;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import lombok.Data;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecipeDto {
    private String name;
    private String category; // например, "Завтрак"
    private List<RecipeIngredientDto> ingredients;
    private double totalCalories;

    public static RecipeDto fromEntity(Recipe r) {
        RecipeDto dto = new RecipeDto();
        dto.setName(r.getName());
        dto.setCategory(r.getCategory() != null ? r.getCategory().getDisplayName() : "Без категории");

        // Маппим ингредиенты
        if (r.getIngredients() != null) {
            dto.setIngredients(
                    r.getIngredients().stream()
                            .map(RecipeIngredientDto::fromEntity)
                            .collect(Collectors.toList())
            );
        } else {
            dto.setIngredients(List.of());
        }

        // Считаем калории
        dto.setTotalCalories(r.calculateTotalCalories());
        return dto;
    }
}



