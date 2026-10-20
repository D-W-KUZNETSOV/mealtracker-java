package com.e.mealtracker.dto;

import com.e.mealtracker.domain.Ingredient;
import com.e.mealtracker.util.UnitType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

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
    private String category;

    // ============ Единицы измерения ============
    private UnitType unitType;
    private Double unitWeightGrams;
    private BigDecimal gramsPerTbsp;
    private BigDecimal gramsPerTsp;
    private String barcode;


    public static IngredientDto fromEntity(Ingredient i) {
        return IngredientDto.builder()
                .id(i.getId())
                .name(i.getName())
                .caloriesPer100g(i.calculateCaloriesPer100g())
                .fatsPer100g(i.getFatsPer100g())
                .proteinsPer100g(i.getProteinsPer100g())
                .carbsPer100g(i.getCarbsPer100g())
                .category(i.getCategory())
                .unitType(i.getUnitType())
                .unitWeightGrams(i.getUnitWeightGrams())
                .gramsPerTbsp(i.getGramsPerTbsp())
                .gramsPerTsp(i.getGramsPerTsp())
                .barcode(i.getBarcode())
                .build();
    }
}