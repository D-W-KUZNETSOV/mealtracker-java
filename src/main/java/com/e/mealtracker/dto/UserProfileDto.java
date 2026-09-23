package com.e.mealtracker.dto;

import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.Gender;   // ← добавить
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserProfileDto {

    private Integer heightCm;
    private BigDecimal currentWeightKg;
    private BigDecimal targetWeightKg;

    // ✅ БЫЛО: private String gender;
    private Gender gender;

    @Schema(allowableValues = {"SEDENTARY", "LIGHT", "MODERATE", "HIGH", "VERY_HIGH"})
    private ActivityLevel activityLevel;

    private Integer ageYears;
    private BigDecimal bmi;
}

