package com.e.mealtracker.controller;

import com.e.mealtracker.dto.RecipeIngredientInput;
import com.e.mealtracker.service.RecipeIngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recipes/{recipeId}/ingredients")
@SecurityRequirement(name = "BearerAuth")
public class RecipeIngredientController {

    private final RecipeIngredientService service;

    /**
     * Добавляет ингредиент в рецепт с указанным весом (в граммах).
     * Принимает ID рецепта из пути и DTO с ingredientId и quantityGrams из тела запроса.
     * Возвращает простое сообщение об успехе (200 OK).
     */
    @PostMapping
    @Operation(summary = "Добавить ингредиент в рецепт")
    public ResponseEntity<String> addIngredient(
            @PathVariable Long recipeId,
            @RequestBody RecipeIngredientInput input) {
        service.addIngredientToRecipe(recipeId, input.getIngredientId(), input.getQuantityGrams());
        return ResponseEntity.ok("Ингредиент добавлен в рецепт");
    }

    /**
     * Обновляет вес ингредиента в рецепте.
     * Проверяет, что ID ингредиента в URL совпадает с ID в теле запроса.
     * При несовпадении возвращает 400 Bad Request.
     * При успехе обновляет вес через сервис и возвращает сообщение (200 OK).
     */
    @PutMapping("/{ingredientId}")
    @Operation(summary = "Обновить вес ингредиента в рецепте")
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


