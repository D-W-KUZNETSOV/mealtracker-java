package com.e.mealtracker.controller;

import com.e.mealtracker.dto.DailyStatsDto;
import com.e.mealtracker.dto.RecipePortionRequest;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.repository.UserRepository;
import com.e.mealtracker.service.StatsService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class StatsController {

    private final StatsService statsService;
    private final UserRepository userRepository;

    /**
     * Добавляет порцию блюда (рецепта) в дневной лог текущего пользователя.
     * Принимает DTO RecipePortionRequest (recipeId, weightInGrams).
     * Считает КБЖУ порции, обновляет дневные итоги и возвращает обновлённую статистику за сегодня (200 OK).
     */
    @PostMapping("/daily/add")
    @Operation(summary = "Добавить порцию рецепта в дневной лог и получить обновлённую статистику")
    public ResponseEntity<DailyStatsDto> addPortion(
            @RequestBody RecipePortionRequest portion,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        DailyStatsDto stats = statsService.addPortionAndReturnTodayStats(portion, user);
        return ResponseEntity.ok(stats);
    }

    /**
     * Получает дневную статистику текущего пользователя за сегодня (КБЖУ, % от цели по белку).
     * Возвращает DailyStatsDto (200 OK). Если лога за сегодня нет, возвращает нулевые значения.
     */
    @GetMapping("/daily")
    @Operation(summary = "Получить дневную статистику за сегодня")
    public ResponseEntity<DailyStatsDto> getTodayStats(
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        DailyStatsDto stats = statsService.getTodayStats(user);
        return ResponseEntity.ok(stats);
    }

    /**
     * Получает дневную статистику за конкретную дату (в формате ISO: yyyy-MM-dd).
     * Используется для просмотра истории питания пользователя.
     * Возвращает DailyStatsDto (200 OK).
     */
    @GetMapping("/daily/{date}")
    @Operation(summary = "Получить дневную статистику за конкретную дату")
    public ResponseEntity<DailyStatsDto> getStatsByDate(
            @PathVariable String date,
            @AuthenticationPrincipal UserDetails userDetails) {
        User user = getUser(userDetails);
        LocalDate parsedDate = LocalDate.parse(date);
        DailyStatsDto stats = statsService.getStatsByDate(parsedDate, user);
        return ResponseEntity.ok(stats);
    }

    /**
     * Вспомогательный метод для получения сущности User из объекта UserDetails (контекст безопасности).
     * Ищет пользователя по username в базе. Если не найден — выбрасывает UsernameNotFoundException.
     */
    private User getUser(UserDetails userDetails) {
        return userRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Пользователь не найден: " + userDetails.getUsername()));
    }
}

