package com.e.mealtracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.util.List;

@Data
public class CreateRecipeRequest {
    @NotNull(message = "Название рецепта обязательно")
    private String name;

    private String category;

    @NotEmpty(message = "В рецепте должен быть хотя бы один ингредиент")
    @Valid
    private List<IngredientWeightDto> ingredients;
}

