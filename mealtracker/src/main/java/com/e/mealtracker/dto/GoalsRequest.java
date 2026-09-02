package com.e.mealtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoalsRequest {
    @NotNull(message = "Вес обязателен")
    @Min(value = 40, message = "Вес должен быть не менее 40 кг")
    private double currentWeightKg;

    @NotNull(message = "Норма белка на кг обязательна")
    @Min(value = 0, message = "Норма белка не может быть отрицательной")
    private double proteinPerKg;

    private Integer targetCalories;
}
