package com.e.mealtracker.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateIngredientRequest {
    @NotBlank(message = "Название ингредиента обязательно")
    private String name;

    @Min(value = 0, message = "Калории не могут быть отрицательными")
    private Double caloriesPer100g;

    @Min(value = 0, message = "Калории не могут быть отрицательными")
    private Double proteinsPer100g;
    @Min(value = 0, message = "Калории не могут быть отрицательными")
    private Double fatsPer100g;
    @Min(value = 0, message = "Калории не могут быть отрицательными")
    private Double carbsPer100g;
}

