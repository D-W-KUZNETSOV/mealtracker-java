package com.e.mealtracker.dto;

import com.e.mealtracker.util.UnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientDto {
    private Long id;
    private String name;
    private Double caloriesPer100g;
    private Double proteinsPer100g;
    private Double fatsPer100g;
    private Double carbsPer100g;

    // ============ Единицы измерения ============
    private UnitType unitType;
    private Double unitWeightGrams;
}