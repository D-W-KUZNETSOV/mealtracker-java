package com.e.mealtracker.dto;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.util.UnitType;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class IngredientResponseDto {
    private Long id;
    private String name;
    private double fatsPer100g;
    private double proteinsPer100g;
    private double carbsPer100g;
    private double caloriesPer100g;
    private String category;   // 🆕
    private String barcode;    // 🆕

    // ============ Единицы измерения ============
    private UnitType unitType;
    private Double unitWeightGrams;

    // 🆕 Добавили ложки
    private BigDecimal gramsPerTbsp;
    private BigDecimal gramsPerTsp;

    public static IngredientResponseDto fromEntity(Ingredient ingredient) {
        IngredientResponseDto dto = new IngredientResponseDto();
        dto.setId(ingredient.getId());
        dto.setName(ingredient.getName());
        dto.setFatsPer100g(ingredient.getFatsPer100g() != null ? ingredient.getFatsPer100g() : 0.0);
        dto.setProteinsPer100g(ingredient.getProteinsPer100g() != null ? ingredient.getProteinsPer100g() : 0.0);
        dto.setCarbsPer100g(ingredient.getCarbsPer100g() != null ? ingredient.getCarbsPer100g() : 0.0);
        dto.setCaloriesPer100g(ingredient.calculateCaloriesPer100g());

        // ============ Единицы измерения ============
        dto.setUnitType(ingredient.getUnitType());
        dto.setUnitWeightGrams(ingredient.getUnitWeightGrams());

        // 🆕 Прокидываем ложки
        dto.setGramsPerTbsp(ingredient.getGramsPerTbsp());
        dto.setGramsPerTsp(ingredient.getGramsPerTsp());
        dto.setCategory(ingredient.getCategory());
        dto.setBarcode(ingredient.getBarcode());

        return dto;
    }
}