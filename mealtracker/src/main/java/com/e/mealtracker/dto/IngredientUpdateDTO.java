package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class IngredientUpdateDTO {
    private String name;
    private Double fatsPer100g;
    private Double proteinsPer100g;
    private Double carbsPer100g;
}
