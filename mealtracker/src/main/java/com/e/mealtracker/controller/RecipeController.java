package com.e.mealtracker.controller;

import com.e.mealtracker.dto.*;
import com.e.mealtracker.exception.RecipeNotFoundException;
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
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeNutritionService recipeNutritionService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

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
        try {
            recipeService.deleteRecipeByUser(id, username);
            log.info("Рецепт удалён: id={}, username={}", id, username);
            return ResponseEntity.ok(new ApiResponse("success", "Рецепт успешно удалён"));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<Map<String, String>> handleNotFound(RecipeNotFoundException ex) {
        log.warn("Бизнес-ошибка (не найдено): {}", ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
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

