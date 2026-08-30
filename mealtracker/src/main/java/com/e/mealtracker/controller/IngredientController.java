package com.e.mealtracker.controller;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.CreateIngredientRequest;
import com.e.mealtracker.dto.IngredientDto;
import com.e.mealtracker.service.IngredientService;  // <-- важно: подключаем сервис
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;  // <-- правильно: сервис, а не контроллер

    @PostMapping
    public IngredientDto createIngredient(@Valid @RequestBody CreateIngredientRequest request) {
        Ingredient ingredient = ingredientService.saveIngredient(request);
        return ingredientToDto(ingredient);
    }

    // Простой маппер внутри контроллера (для личного проекта ок)
    private IngredientDto ingredientToDto(Ingredient ingredient) {
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .caloriesPer100g(ingredient.getCaloriesPer100g())
                .build();
    }
}

