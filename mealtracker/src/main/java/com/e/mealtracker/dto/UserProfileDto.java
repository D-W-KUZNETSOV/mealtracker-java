package com.e.mealtracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class UserProfileDto {
    @Schema(example = "178")
    private Integer heightCm;

    @Schema(example = "80.5")
    private BigDecimal currentWeightKg;

    @Schema(example = "75.0")
    private BigDecimal targetWeightKg;

    @Schema(allowableValues = {"MALE", "FEMALE"}, example = "MALE")
    private String gender;

    @Schema(allowableValues = {"LOW", "MODERATE", "HIGH"}, example = "HIGH")
    private String activityLevel;

    @Schema(example = "30")
    private Integer ageYears;

    @Schema(example = "25.4", description = "BMI = weight(kg) / (height(m)^2)")
    private BigDecimal bmi;
}

