package com.e.mealtracker.controller;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.GoalsRequest;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.service.NutritionCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth") // <-- замок на эндпоинты в Swagger
public class NutritionController {

    private final NutritionCalculationService calculationService;

    @PostMapping("/goals")
    @Operation(summary = "Установить или обновить цели пользователя (вес, белок, калории, активность)")
    public ResponseEntity<UserGoals> setGoals(@Valid @RequestBody GoalsRequest request,
                                              Authentication authentication) {
        String username = authentication.getName(); // <-- из JWT

        UserGoals saved = calculationService.setUserGoals(
                username,
                request.getCurrentWeightKg(),
                request.getProteinPerKg(),
                request.getTargetCalories(),
                request.getActivityLevel()
        );
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/target-protein")
    @Operation(summary = "Получить целевое количество белка на основе сохранённых целей")
    public ResponseEntity<TargetProteinResponse> getTargetProtein(Authentication authentication) {
        String username = authentication.getName();

        TargetProteinResponse response =
                calculationService.calculateTargetProteinFromGoals(username);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goals")
    @Operation(summary = "Получить текущие цели пользователя")
    public ResponseEntity<?> getCurrentGoals(Authentication authentication) {
        String username = authentication.getName();

        var opt = calculationService.getCurrentGoals(username);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }
}


