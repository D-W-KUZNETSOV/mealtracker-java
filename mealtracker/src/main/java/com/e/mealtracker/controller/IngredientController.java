package com.e.mealtracker.controller;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.CreateIngredientRequest;
import com.e.mealtracker.dto.IngredientDto;
import com.e.mealtracker.dto.IngredientResponseDto;
import com.e.mealtracker.dto.IngredientUpdateDTO;
import com.e.mealtracker.service.IngredientService;  // <-- важно: подключаем сервис
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;  // <-- правильно: сервис, а не контроллер

    @PostMapping
    public IngredientDto createIngredient(@Valid @RequestBody CreateIngredientRequest request) {
        Ingredient ingredient = ingredientService.saveIngredient(request);
        return ingredientToDto(ingredient);
    }
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ингредиент по ID")
    public ResponseEntity<Void> deleteIngredient(@PathVariable Long id) {
        if (!ingredientService.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        ingredientService.deleteById(id);
        return ResponseEntity.noContent().build(); // 204 No Content
    }
    @PutMapping("/{id}")
    @Operation(summary = "Обновить ингредиент по ID")
    public ResponseEntity<IngredientResponseDto> updateIngredient(
            @PathVariable Long id,
            @RequestBody IngredientUpdateDTO dto) {

        IngredientResponseDto updated = ingredientService.updateById(id, dto);
        return ResponseEntity.ok(updated);
    }

    // Простой маппер внутри контроллера (для личного проекта ок)
    private IngredientDto ingredientToDto(Ingredient ingredient) {
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .caloriesPer100g(ingredient.getCaloriesPer100g())
                .build();
    }

}

