package com.e.mealtracker.controller;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.GoalsRequest;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.service.NutritionCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
public class NutritionController {

    private final NutritionCalculationService calculationService;

    @PostMapping("/goals")
    @Operation(summary = "Установить или обновить цели пользователя (вес, белок, калории, активность)")
    @ApiResponse(responseCode = "200", description = "Цели успешно сохранены")
    @ApiResponse(responseCode = "400", description = "Некорректные данные")
    public ResponseEntity<UserGoals> setGoals(@Valid @RequestBody GoalsRequest request) {
        UserGoals saved = calculationService.setUserGoals(
                request.getCurrentWeightKg(),
                request.getProteinPerKg(),
                request.getTargetCalories(),
                request.getActivityLevel()  // Добавляем новый параметр
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/target-protein")
    @Operation(summary = "Получить целевое количество белка на основе сохранённых целей")
    @ApiResponse(responseCode = "200", description = "Возвращены текущий вес и целевая норма белка")
    public ResponseEntity<TargetProteinResponse> getTargetProtein() {
        TargetProteinResponse response = calculationService.calculateTargetProteinFromGoals();
        return ResponseEntity.ok(response);
    }
    @GetMapping("/goals")
    @Operation(summary = "Получить текущие цели пользователя")
    @ApiResponse(responseCode = "200", description = "Текущие цели возвращены")
    @ApiResponse(responseCode = "404", description = "Цели ещё не установлены")
    public ResponseEntity<?> getCurrentGoals() {
        var opt = calculationService.getCurrentGoals();
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }


}

