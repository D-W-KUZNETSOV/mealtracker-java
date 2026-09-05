package com.e.mealtracker.controller;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.dto.CreateIngredientRequest;
import com.e.mealtracker.dto.IngredientDto;
import com.e.mealtracker.dto.IngredientResponseDto;
import com.e.mealtracker.dto.IngredientUpdateDTO;
import com.e.mealtracker.service.IngredientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@SecurityRequirement(name = "BearerAuth")
@RequestMapping("/api/ingredients")
@RequiredArgsConstructor
public class IngredientController {

    private final IngredientService ingredientService;

    @PostMapping
    public IngredientDto createIngredient(
            @Valid @RequestBody CreateIngredientRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        Ingredient ingredient = ingredientService.saveIngredient(request, userDetails.getUsername());
        return ingredientToDto(ingredient);
    }

    @GetMapping
    @Operation(summary = "Получить все ингредиенты текущего пользователя")
    public List<IngredientDto> getAllIngredients(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ingredientService.findAllByUsername(userDetails.getUsername())
                .stream()
                .map(this::ingredientToDto)
                .toList();
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить ингредиент по ID")
    public ResponseEntity<Void> deleteIngredient(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        if (!ingredientService.existsById(id, userDetails.getUsername())) {
            return ResponseEntity.notFound().build();
        }
        ingredientService.deleteById(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить ингредиент по ID")
    public ResponseEntity<IngredientResponseDto> updateIngredient(
            @PathVariable Long id,
            @RequestBody IngredientUpdateDTO dto,
            @AuthenticationPrincipal UserDetails userDetails) {
        IngredientResponseDto updated = ingredientService.updateById(id, dto, userDetails.getUsername());
        return ResponseEntity.ok(updated);
    }

    private IngredientDto ingredientToDto(Ingredient ingredient) {
        return IngredientDto.builder()
                .id(ingredient.getId())
                .name(ingredient.getName())
                .caloriesPer100g(ingredient.getCaloriesPer100g())
                .build();
    }
}

