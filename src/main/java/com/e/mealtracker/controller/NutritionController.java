package com.e.mealtracker.controller;

import com.e.mealtracker.domain.UserGoals;
import com.e.mealtracker.dto.GoalsRequest;
import com.e.mealtracker.dto.TargetProteinResponse;
import com.e.mealtracker.dto.UserGoalsDto;
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

    /**
     * Устанавливает или обновляет персональные цели пользователя: текущий вес,
     * норма белка на кг веса, целевые калории и уровень активности.
     * Принимает валидированный DTO GoalsRequest и объект Authentication для получения имени пользователя.
     * Возвращает сохранённые цели (UserGoals) в формате 200 OK.
     */
    @PostMapping("/goals")
    @Operation(summary = "Установить или обновить цели пользователя (вес, белок, калории, активность)")
    public ResponseEntity<UserGoalsDto> setGoals(@Valid @RequestBody GoalsRequest request,
                                                 Authentication authentication) {
        User user = getUser(authentication);

        UserGoalsDto saved = calculationService.setUserGoals(
                user,
                request.getCurrentWeightKg(),
                request.getProteinPerKg(),
                request.getTargetCalories(),
                request.getActivityLevel()
        );
        return ResponseEntity.ok(saved);
    }

    /**
     * Рассчитывает и возвращает целевое количество белка для пользователя
     * на основе сохранённых целей (вес и норма белка на кг).
     * Использует объект Authentication для идентификации пользователя.
     * Результат возвращается в виде TargetProteinResponse (200 OK).
     */
    @GetMapping("/target-protein")
    @Operation(summary = "Получить целевое количество белка на основе сохранённых целей")
    public ResponseEntity<TargetProteinResponse> getTargetProtein(Authentication authentication) {
        User user = getUser(authentication);

        TargetProteinResponse response = calculationService.calculateTargetProteinFromGoals(user);
        return ResponseEntity.ok(response);
    }

    /**
     * Получает текущие сохранённые цели пользователя (вес, калории, активность и т.д.).
     * Если цели не заданы, возвращает ответ 404 Not Found.
     * Для идентификации пользователя используется объект Authentication.
     */
    @GetMapping("/goals")
    @Operation(summary = "Получить текущие цели пользователя")
    public ResponseEntity<UserGoalsDto> getCurrentGoals(Authentication authentication) {
        User user = getUser(authentication);

        return calculationService.getCurrentGoals(user)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /**
     * Вспомогательный метод для получения сущности User по имени из объекта Authentication.
     * Если пользователь не найден в БД, выбрасывает UsernameNotFoundException.
     * Используется во всех методах контроллера для привязки данных к конкретному пользователю.
     */
    private User getUser(Authentication authentication) {
        return userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден: " + authentication.getName()));
    }
}



