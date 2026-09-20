package com.e.mealtracker.controller;

import com.e.mealtracker.dto.UserProfileDto;
import com.e.mealtracker.dto.UserProfileUpdateDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.service.NutritionCalculationService;
import com.e.mealtracker.service.ProfileService;
import com.e.mealtracker.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
@SecurityRequirement(name = "BearerAuth")
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final NutritionCalculationService nutritionCalculationService;

    /**
     * Возвращает расчётную дневную норму калорий для текущего пользователя.
     * Достаёт профиль пользователя из базы и передаёт его в NutritionCalculationService.
     * Результат — BigDecimal с количеством ккал (200 OK).
     */
    @GetMapping("/calories/daily")
    @Operation(summary = "Получить дневную норму калорий текущего пользователя")
    public ResponseEntity<BigDecimal> getDailyCalories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        UserProfile profile = user.getProfile();
        BigDecimal calories = nutritionCalculationService.calculateDailyCalories(profile);
        return ResponseEntity.ok(calories);
    }

    /**
     * Обновляет профиль текущего пользователя (имя, пол, возраст, рост, вес и т.д.).
     * Принимает валидированный DTO UserProfileUpdateDto.
     * При успешном обновлении возвращает 204 No Content.
     */
    @PutMapping
    @Operation(summary = "Обновить профиль текущего пользователя")
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                              @Valid @RequestBody UserProfileUpdateDto dto) {
        profileService.updateProfile(userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }

    /**
     * Получает профиль текущего пользователя в виде DTO.
     * Возвращает данные профиля (200 OK): личная информация, антропометрия и т.п.
     */
    @GetMapping
    @Operation(summary = "Получить профиль текущего пользователя")
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDto dto = profileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(dto);
    }
}


