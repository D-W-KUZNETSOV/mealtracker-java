package com.e.mealtracker.dto;

import lombok.Data;

@Data
public class ImportIngredientDto {
    private String name;
    private Double weightInGrams;
}