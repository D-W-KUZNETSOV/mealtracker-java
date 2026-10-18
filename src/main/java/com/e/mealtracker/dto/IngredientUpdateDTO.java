package com.e.mealtracker.dto;

import com.e.mealtracker.util.UnitType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientUpdateDTO {
    private String name;
    private Double fatsPer100g;
    private Double proteinsPer100g;
    private Double carbsPer100g;
    private Double caloriesPer100g;

    // зачем: единицы измерения (если юзер хочет поменять)
    private UnitType unitType;
    private Double unitWeightGrams;
    private BigDecimal gramsPerTbsp;
    private BigDecimal gramsPerTsp;
}