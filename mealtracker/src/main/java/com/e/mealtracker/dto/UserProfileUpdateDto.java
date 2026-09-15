package com.e.mealtracker.dto;

import com.e.mealtracker.util.ActivityLevel;
import com.e.mealtracker.util.Gender;   // ← добавить
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserProfileUpdateDto {

    private LocalDate dateOfBirth;

    @Min(50) @Max(300)
    private Integer heightCm;

    @DecimalMin("20.0")
    private BigDecimal currentWeightKg;

    @DecimalMin("20.0")
    private BigDecimal targetWeightKg;

    // ✅ БЫЛО: @Pattern(regexp = "^(MALE|FEMALE)$") private String gender;
    private Gender gender;

    @Schema(allowableValues = {"SEDENTARY", "LIGHT", "MODERATE", "HIGH", "VERY_HIGH"})
    private ActivityLevel activityLevel;
}

