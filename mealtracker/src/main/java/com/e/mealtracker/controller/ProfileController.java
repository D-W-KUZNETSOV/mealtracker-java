package com.e.mealtracker.controller;

import com.e.mealtracker.dto.UserProfileDto;
import com.e.mealtracker.dto.UserProfileUpdateDto;
import com.e.mealtracker.entity.User;
import com.e.mealtracker.entity.UserProfile;
import com.e.mealtracker.service.NutritionCalculationService;
import com.e.mealtracker.service.ProfileService;
import com.e.mealtracker.service.UserService;
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
public class ProfileController {

    private final UserService userService;
    private final ProfileService profileService;
    private final NutritionCalculationService nutritionCalculationService;

    @GetMapping("/calories/daily")
    public ResponseEntity<BigDecimal> getDailyCalories(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByUsername(userDetails.getUsername());
        UserProfile profile = user.getProfile();
        BigDecimal calories = nutritionCalculationService.calculateDailyCalories(profile);
        return ResponseEntity.ok(calories);
    }

    @PutMapping
    public ResponseEntity<Void> updateProfile(@AuthenticationPrincipal UserDetails userDetails,
                                              @Valid @RequestBody UserProfileUpdateDto dto) {
        profileService.updateProfile(userDetails.getUsername(), dto);
        return ResponseEntity.noContent().build();
    }
    @GetMapping
    public ResponseEntity<UserProfileDto> getProfile(@AuthenticationPrincipal UserDetails userDetails) {
        UserProfileDto dto = profileService.getProfile(userDetails.getUsername());
        return ResponseEntity.ok(dto);
    }


}

