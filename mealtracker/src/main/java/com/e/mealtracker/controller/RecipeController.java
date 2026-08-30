package com.e.mealtracker.controller;

import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@RequestBody CreateRecipeRequest request) {
        RecipeDto saved = recipeService.saveRecipe(request);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<RecipeDto> getAllRecipes() {
        return recipeService.getAllRecipes();
    }
}
