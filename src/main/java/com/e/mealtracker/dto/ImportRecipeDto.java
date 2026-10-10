package com.e.mealtracker.dto;

import lombok.Data;

import java.util.List;

@Data
public class ImportRecipeDto {
    private String name;
    private String category;
    private List<ImportIngredientDto> ingredients;
    private List<String> steps;
}