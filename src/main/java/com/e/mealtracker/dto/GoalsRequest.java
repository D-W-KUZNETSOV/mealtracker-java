package com.e.mealtracker.dto;

import com.e.mealtracker.util.ActivityLevel;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class GoalsRequest {

    @Schema(description = "Текущий вес в кг",
            example = "75.5",
            required = true)
    @NotNull(message = "Вес обязателен")
    @Positive(message = "Вес должен быть положительным")
    private Double currentWeightKg;

    @Schema(description = "Количество белка на кг веса",
            example = "2.0",
            allowableValues = {"1.5", "1.8", "2.0", "2.2", "2.5"})
    private Double proteinPerKg;

    @Schema(description = "Целевые калории",
            example = "2000",
            allowableValues = {"1500", "1800", "2000", "2200", "2500", "2800", "3000"})
    private Integer targetCalories;

    @Schema(description = "Уровень физической активности",
            example = "MODERATE",
            required = true)
    @NotNull(message = "Уровень активности обязателен")
    private ActivityLevel activityLevel;

    // Геттеры и сеттеры
    public Double getCurrentWeightKg() {
        return currentWeightKg;
    }

    public void setCurrentWeightKg(Double currentWeightKg) {
        this.currentWeightKg = currentWeightKg;
    }

    public Double getProteinPerKg() {
        return proteinPerKg;
    }

    public void setProteinPerKg(Double proteinPerKg) {
        this.proteinPerKg = proteinPerKg;
    }

    public Integer getTargetCalories() {
        return targetCalories;
    }

    public void setTargetCalories(Integer targetCalories) {
        this.targetCalories = targetCalories;
    }

    public ActivityLevel getActivityLevel() {
        return activityLevel;
    }

    public void setActivityLevel(ActivityLevel activityLevel) {
        this.activityLevel = activityLevel;
    }
}

