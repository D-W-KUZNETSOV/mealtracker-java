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
import org.springframework.data.domain.Sort;
import java.math.BigDecimal;
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
    @Operation(summary = "Список рецептов с пагинацией и фильтрами")
    public ResponseEntity<Page<RecipeDto>> getAllRecipes(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String query,
            @RequestParam(required = false) BigDecimal minCalories,
            @RequestParam(required = false) BigDecimal maxCalories,
            @RequestParam(required = false) BigDecimal minProtein,
            @AuthenticationPrincipal UserDetails userDetails,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "name,asc") String sort) {

        String username = userDetails.getUsername();

        // Парсим sort: "name,asc" → Sort
        // Маппим фронтовые имена в SQL-имена (для native query)
        Sort sortObj = Sort.by(Sort.Direction.ASC, "name");
        if (sort != null && !sort.isBlank()) {
            String[] parts = sort.split(",");
            String field = parts[0];
            Sort.Direction dir = parts.length > 1 && parts[1].equalsIgnoreCase("desc")
                    ? Sort.Direction.DESC
                    : Sort.Direction.ASC;

            String sqlField = switch (field) {
                case "calories" -> "total_calories";
                case "protein" -> "total_proteins";
                case "name" -> "name";
                default -> "name";
            };

            sortObj = Sort.by(dir, sqlField);
        }

        Page<RecipeDto> recipes = recipeService.getAllRecipesByUser(
                category, query, minCalories, maxCalories, minProtein,
                username, PageRequest.of(page, size, sortObj));

        log.debug("Список рецептов: page={}, size={}, count={}, username={}",
                page, size, recipes.getTotalElements(), username);
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

    @PutMapping("/{id}")
    @Operation(summary = "Обновить рецепт")
    public ResponseEntity<RecipeDto> updateRecipe(
            @PathVariable Long id,
            @Valid @RequestBody CreateRecipeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        RecipeDto updated = recipeService.updateRecipe(id, request, username);
        log.info("Рецепт обновлён: id={}, username={}", id, username);
        return ResponseEntity.ok(updated);
    }


}


