package com.e.mealtracker.controller;

import com.e.mealtracker.dto.*;
import com.e.mealtracker.service.RecipeNutritionService;
import com.e.mealtracker.service.RecipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
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
@SecurityRequirement(name = "BearerAuth")
public class RecipeController {

    private final RecipeService recipeService;
    private final RecipeNutritionService recipeNutritionService;

    /**
     * Получает постраничный список рецептов текущего пользователя.
     * Поддерживает фильтрацию по категории (MealType) и параметры пагинации (page, size).
     * Возвращает страницу DTO рецептов (200 OK).
     */
    @GetMapping
    @Operation(summary = "Получить список рецептов текущего пользователя с пагинацией и фильтром по категории")
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

    /**
     * Создаёт новый рецепт от имени текущего пользователя.
     * Принимает валидированный DTO CreateRecipeRequest с названием, описанием,
     * категорией, списком ингредиентов и их весами.
     * Возвращает DTO созданного рецепта со статусом 201 Created.
     */
    @PostMapping
    @Operation(summary = "Создать новый рецепт")
    public ResponseEntity<RecipeDto> createRecipe(
            @Valid @RequestBody CreateRecipeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        RecipeDto saved = recipeService.saveRecipe(request, username);
        log.info("Рецепт создан: id={}, username={}", saved.getId(), username);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    /**
     * Получает сводку по нутритивной ценности конкретного рецепта (КБЖУ, вес и т.д.).
     * Принадлежность рецепта пользователю проверяется в сервисе.
     * Возвращает RecipeSummaryDto (200 OK).
     */
    @GetMapping("/{id}/summary")
    @Operation(summary = "Получить сводку нутритивной ценности рецепта по ID")
    public ResponseEntity<RecipeSummaryDto> getRecipeSummary(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        RecipeSummaryDto summary = recipeNutritionService.getRecipeSummary(id, userDetails.getUsername());
        return ResponseEntity.ok(summary);
    }

    /**
     * Удаляет рецепт по ID, если он принадлежит текущему пользователю.
     * Если рецепт не найден или чужой — сервис выбросит исключение.
     * При успехе возвращает ApiResponse с подтверждением (200 OK).
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить рецепт по ID")
    public ResponseEntity<ApiResponse> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        recipeService.deleteRecipeByUser(id, username);
        log.info("Рецепт удалён: id={}, username={}", id, username);
        return ResponseEntity.ok(new ApiResponse("success", "Рецепт успешно удалён"));
    }

    /**
     * Получает список всех публичных рецептов (доступно без привязки к пользователю).
     * Возвращает список RecipeResponse (200 OK).
     */
    @GetMapping("/public")
    @Operation(summary = "Получить все публичные рецепты")
    public ResponseEntity<List<RecipeResponse>> getPublicRecipes() {
        var recipes = recipeService.getPublicRecipes();
        return ResponseEntity.ok(recipes);
    }

    /**
     * Переключает видимость рецепта (PUBLIC <-> PRIVATE).
     * Доступно только владельцу рецепта — принадлежность проверяется в сервисе.
     * Возвращает обновлённый RecipeResponse с новым статусом видимости (200 OK).
     */
    @PatchMapping("/{id}/visibility")
    @Operation(summary = "Переключить видимость рецепта (публичный/приватный)")
    public ResponseEntity<RecipeResponse> toggleVisibility(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(recipeService.toggleRecipeVisibility(id, userDetails.getUsername()));
    }
}


