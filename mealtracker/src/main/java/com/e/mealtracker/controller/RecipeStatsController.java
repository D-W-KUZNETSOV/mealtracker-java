package com.e.mealtracker.controller;

import com.e.mealtracker.dto.RecipeStatsDto;
import com.e.mealtracker.service.RecipeStatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class RecipeStatsController {

    private final RecipeStatsService statsService;

    /**
     * Получает статистику по конкретному рецепту (КБЖУ на 100 г, общий вес, порции и т.д.).
     * Принимает ID рецепта в пути запроса. Расчёт выполняется в RecipeStatsService.
     * Возвращает RecipeStatsDto с агрегированными данными (200 OK).
     */
    @GetMapping("/api/recipes/{recipeId}/stats")
    @Operation(summary = "Получить статистику по рецепту (КБЖУ, вес, порции)")
    public ResponseEntity<RecipeStatsDto> getStats(@PathVariable Long recipeId) {
        RecipeStatsDto stats = statsService.calculateStatsForRecipe(recipeId);
        return ResponseEntity.ok(stats);
    }
}


