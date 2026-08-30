package com.e.mealtracker.controller;

import com.e.mealtracker.dto.CreateRecipeRequest;
import com.e.mealtracker.dto.RecipeDto;
import com.e.mealtracker.service.RecipeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/recipes")
@RequiredArgsConstructor
public class RecipeController {

    private final RecipeService recipeService;

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            // Берем только имя поля, без пути (например, "weightInGrams" вместо "ingredients[0].weightInGrams")
            String fieldName = error.getField();
            errors.put(fieldName, error.getDefaultMessage());
        });
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }


    @GetMapping
    public List<RecipeDto> getAllRecipes( @RequestParam(required = false) String category) {
        return recipeService.getAllRecipes(category);
    }
    @PostMapping
    public ResponseEntity<RecipeDto> createRecipe(@Valid @RequestBody CreateRecipeRequest request) {
        RecipeDto saved = recipeService.saveRecipe(request);
        return ResponseEntity.ok(saved);
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecipe(@PathVariable Long id) {
        try {
            recipeService.deleteRecipe(id);
            return ResponseEntity.ok("Рецепт успешно удалён");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Не удалось удалить: " + e.getMessage());
        }
    }

}

