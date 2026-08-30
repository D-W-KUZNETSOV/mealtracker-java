package com.e.mealtracker.controller;

import com.e.mealtracker.dto.RecipeStatsDto;
import com.e.mealtracker.service.RecipeStatsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RecipeStatsController {

    private final RecipeStatsService statsService;

    @GetMapping("/api/recipes/{recipeId}/stats")
    public ResponseEntity<RecipeStatsDto> getStats(@PathVariable Long recipeId) {
        RecipeStatsDto stats = statsService.calculateStatsForRecipe(recipeId);
        return ResponseEntity.ok(stats);
    }
}

