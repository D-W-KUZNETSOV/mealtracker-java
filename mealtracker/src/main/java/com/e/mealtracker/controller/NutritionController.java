package com.e.mealtracker.controller;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.GoalsRequest;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.UserRepository;
import com.e.mealtracker.service.NutritionCalculationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/nutrition")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class NutritionController {

    private final NutritionCalculationService calculationService;
    private final UserRepository userRepository;

    @PostMapping("/goals")
    @Operation(summary = "Установить или обновить цели пользователя (вес, белок, калории, активность)")
    public ResponseEntity<UserGoals> setGoals(@Valid @RequestBody GoalsRequest request,
                                              Authentication authentication) {
        User user = getUser(authentication);

        UserGoals saved = calculationService.setUserGoals(
                user,
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
        User user = getUser(authentication);

        TargetProteinResponse response = calculationService.calculateTargetProteinFromGoals(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/goals")
    @Operation(summary = "Получить текущие цели пользователя")
    public ResponseEntity<?> getCurrentGoals(Authentication authentication) {
        User user = getUser(authentication);

        var opt = calculationService.getCurrentGoals(user);
        if (opt.isPresent()) {
            return ResponseEntity.ok(opt.get());
        }
        return ResponseEntity.notFound().build();
    }

    private User getUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден: " + authentication.getName()));
    }
}



