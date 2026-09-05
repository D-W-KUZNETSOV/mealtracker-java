package com.e.mealtracker.controller;

import com.e.mealtracker.dto.RecipeIngredientInput;
import com.e.mealtracker.service.RecipeIngredientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes/{recipeId}/ingredients")
public class RecipeIngredientController {

    private final RecipeIngredientService service;

    /**
     * Добавить ингредиент в рецепт с весом.
     * POST /api/recipes/1/ingredients
     * Body: {"ingredientId": 1, "quantityGrams": 200}
     */
    @PostMapping
    public ResponseEntity<String> addIngredient(
            @PathVariable Long recipeId,
            @RequestBody RecipeIngredientInput input) {
        service.addIngredientToRecipe(recipeId, input.getIngredientId(), input.getQuantityGrams());
        return ResponseEntity.ok("Ингредиент добавлен в рецепт");
    }

    /**
     * Обновить вес ингредиента в рецепте.
     * PUT /api/recipes/1/ingredients/1
     * Body: {"ingredientId": 1, "quantityGrams": 180}
     */
    @PutMapping("/{ingredientId}")
    public ResponseEntity<String> updateWeight(
            @PathVariable Long recipeId,
            @PathVariable Long ingredientId,
            @RequestBody RecipeIngredientInput input) {

        if (!ingredientId.equals(input.getIngredientId())) {
            return ResponseEntity.badRequest()
                    .body("ID в URL и в теле запроса должны совпадать");
        }

        service.updateWeightInRecipe(recipeId, ingredientId, input.getQuantityGrams());
        return ResponseEntity.ok("Вес обновлён");
    }
}

