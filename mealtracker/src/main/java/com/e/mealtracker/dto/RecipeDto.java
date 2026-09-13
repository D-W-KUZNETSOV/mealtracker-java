package com.e.mealtracker.dto;

import com.e.mealtracker.domain.MealType;
import com.e.mealtracker.domain.Recipe;
import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class RecipeDto {
    private Long id;
    private String name;
    private String category;
    private String description;
    private String imageUrl;
    private List<RecipeIngredientDto> ingredients;
    private BigDecimal totalCalories;
    private BigDecimal totalProteins;
    private BigDecimal totalFats;
    private BigDecimal totalCarbs;

    public static RecipeDto fromEntity(Recipe r) {
        RecipeDto dto = new RecipeDto();
        dto.setId(r.getId());
        dto.setName(r.getName());
        dto.setCategory(r.getCategory() != null ? r.getCategory().getDisplayName() : "Без категории");
        dto.setDescription(r.getDescription());
        dto.setImageUrl(r.getImageUrl());

        // Берём сохранённые значения из колонок, а не пересчитываем
        dto.setTotalCalories(r.getTotalCalories());
        dto.setTotalProteins(r.getTotalProteins());
        dto.setTotalFats(r.getTotalFats());
        dto.setTotalCarbs(r.getTotalCarbs());

        if (r.getIngredients() != null) {
            dto.setIngredients(r.getIngredients().stream()
                    .map(RecipeIngredientDto::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            dto.setIngredients(List.of());
        }

        return dto;
    }
}




