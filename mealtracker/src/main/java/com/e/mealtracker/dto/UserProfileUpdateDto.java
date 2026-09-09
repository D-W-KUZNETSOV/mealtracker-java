package com.e.mealtracker.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileUpdateDto {

    @Min(100)
    @Max(250)
    @Schema(example = "178", description = "Рост в см (100–250)")
    private Integer heightCm;

    @DecimalMin(value = "30.0", inclusive = false)
    @Schema(example = "80.5", description = "Текущий вес в кг (>30)")
    private java.math.BigDecimal currentWeightKg;

    @DecimalMin(value = "30.0", inclusive = false)
    @Schema(example = "75.0", description = "Целевой вес в кг (>30)")
    private java.math.BigDecimal targetWeightKg;

    // Убрали @NotBlank — теперь поле можно не передавать
    @Pattern(regexp = "^(MALE|FEMALE)$")
    @Schema(allowableValues = {"MALE", "FEMALE"}, example = "MALE", description = "Пол")
    private String gender;

    // Убрали @NotBlank — теперь поле можно не передавать
    @Pattern(regexp = "^(LOW|MODERATE|HIGH)$")
    @Schema(allowableValues = {"LOW", "MODERATE", "HIGH"}, example = "MODERATE", description = "Уровень активности")
    private String activityLevel;
}


