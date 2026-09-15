package com.e.mealtracker.controller;

import com.e.mealtracker.dto.*;
import com.e.mealtracker.service.RecipeNutritionService;
import com.e.mealtracker.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeNutritionService recipeNutritionService;

    @GetMapping
    public ResponseEntity<Page<RecipeDto>> getAllRecipes(
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String username = userDetails.getUsername();
        Page<RecipeDto> recipes = recipeService.getAllRecipesByUser(category, username, PageRequest.of(page, size));
        log.debug("Получен список рецептов: page={}, size={}, count={}, username={}", page, size, recipes.getTotalElements(), username);
        return ResponseEntity.ok(recipes);
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(
            @Valid @RequestBody CreateRecipeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        RecipeDto saved = recipeService.saveRecipe(request, username);
        log.info("Рецепт создан: id={}, username={}", saved.getId(), username);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<RecipeSummaryDto> getRecipeSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        RecipeSummaryDto summary = recipeNutritionService.getRecipeSummary(id, userDetails.getUsername());
        return ResponseEntity.ok(summary);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        recipeService.deleteRecipeByUser(id, username);
        log.info("Рецепт удалён: id={}, username={}", id, username);
        return ResponseEntity.ok(new ApiResponse("success", "Рецепт успешно удалён"));
    }

    @GetMapping("/public")
    public ResponseEntity<List<RecipeResponse>> getPublicRecipes() {
        var recipes = recipeService.getPublicRecipes();
        return ResponseEntity.ok(recipes);
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<RecipeResponse> toggleVisibility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(recipeService.toggleRecipeVisibility(id, userDetails.getUsername()));
    }
}

