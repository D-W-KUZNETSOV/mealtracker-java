package com.e.mealtracker.controller;

import com.e.mealtracker.dto.CreateMealPlanItemRequest;
import com.e.mealtracker.dto.CreateMealPlanRequest;
import com.e.mealtracker.dto.MealPlanDto;
import com.e.mealtracker.dto.MealPlanItemDto;
import com.e.mealtracker.service.MealPlanService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/meal-plans")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class MealPlanController {

    private final MealPlanService mealPlanService;

    @GetMapping
    @Operation(summary = "Получить все планы меню")
    public ResponseEntity<List<MealPlanDto>> getAll(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.getAllPlans(userDetails.getUsername()));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить план по ID")
    public ResponseEntity<MealPlanDto> getOne(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.getPlan(id, userDetails.getUsername()));
    }

    @PostMapping
    @Operation(summary = "Создать план меню")
    public ResponseEntity<MealPlanDto> create(
            @RequestBody CreateMealPlanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MealPlanDto dto = mealPlanService.createPlan(
                request.getName(), request.getStartDate(), request.getEndDate(),
                userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить план")
    public ResponseEntity<MealPlanDto> update(
            @PathVariable Long id,
            @RequestBody CreateMealPlanRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(mealPlanService.updatePlan(id,
                request.getName(), request.getStartDate(), request.getEndDate(),
                userDetails.getUsername()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить план")
    public ResponseEntity<Void> delete(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        mealPlanService.deletePlan(id, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Добавить приём в план")
    public ResponseEntity<MealPlanItemDto> addItem(
            @PathVariable Long id,
            @RequestBody CreateMealPlanItemRequest request,
            @AuthenticationPrincipal UserDetails userDetails) {
        MealPlanItemDto dto = mealPlanService.addItem(id,
                request.getPlanDate(), request.getMealType(),
                request.getRecipeId(), request.getIngredientId(),
                request.getServings(), request.getWeightInGrams(),
                request.getCustomName(), userDetails.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    @DeleteMapping("/{planId}/items/{itemId}")
    @Operation(summary = "Удалить приём из плана")
    public ResponseEntity<Void> deleteItem(
            @PathVariable Long planId,
            @PathVariable Long itemId,
            @AuthenticationPrincipal UserDetails userDetails) {
        mealPlanService.deleteItem(planId, itemId, userDetails.getUsername());
        return ResponseEntity.noContent().build();
    }
}
