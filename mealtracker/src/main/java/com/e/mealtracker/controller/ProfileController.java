package com.e.mealtracker.controller;

import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.service.NutritionCalculationService;
import com.e.mealtracker.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/profile")
public class ProfileController {

    private final UserService userService;
    private final NutritionCalculationService nutritionCalculationService;

    public ProfileController(UserService userService, NutritionCalculationService nutritionCalculationService) {
        this.userService = userService;
        this.nutritionCalculationService = nutritionCalculationService;
    }

    @GetMapping("/calories/daily")
    public ResponseEntity<BigDecimal> getDailyCalories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        UserProfile profile = user.getProfile();

        BigDecimal calories = nutritionCalculationService.calculateDailyCalories(profile);
        return ResponseEntity.ok(calories);
    }

}

