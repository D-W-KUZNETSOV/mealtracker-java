package com.e.mealtracker.controller;

import com.e.mealtracker.dto.ApiResponse;
import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @GetMapping
    public List<RecipeDto> getAllRecipes(
            @RequestParam(required = false) String category,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        return recipeService.getAllRecipesByUser(category, username);
    }

    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(
            @Valid @RequestBody CreateRecipeRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();
        RecipeDto saved = recipeService.saveRecipe(request, username);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteRecipe(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        String username = userDetails.getUsername();

        try {
            recipeService.deleteRecipeByUser(id, username);
            return ResponseEntity.ok(new ApiResponse("success", "Рецепт успешно удалён"));
        } catch (IllegalArgumentException e) {
            // Это и есть «не найден или не принадлежит пользователю»
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse("error", e.getMessage()));
        }
    }

    // Опционально: отдельный хендлер для бизнес-ошибок, если они могут быть в других местах
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Бизнес-ошибка: {}", ex.getMessage());
        Map<String, String> body = new HashMap<>();
        body.put("error", ex.getMessage());
        return ResponseEntity.badRequest().body(body);
    }
}


