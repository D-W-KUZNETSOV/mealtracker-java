package com.e.mealtracker.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import java.util.List;

@Data
public class CreateRecipeRequest {

    @NotNull(message = "Название рецепта обязательно")
    @Size(min = 1, max = 200, message = "Название должно быть от 1 до 200 символов")
    private String name;

    private String category;

    @NotEmpty(message = "В рецепте должен быть хотя бы один ингредиент")
    @Valid
    private List<IngredientWeightDto> ingredients;

    // Описание — опционально, но если есть, то не длиннее 2000 символов
    @Size(max = 2000, message = "Описание не должно превышать 2000 символов")
    private String description;

    // Ссылка на картинку — опционально, но если есть, то проверяем длину
    @Size(max = 512, message = "URL картинки не должен превышать 512 символов")
    private String imageUrl;
}


