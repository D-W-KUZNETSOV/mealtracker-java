package com.e.mealtracker.dto;

import com.e.mealtracker.domain.FoodEntry;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FoodEntryDto {

    private final Long id;
    private final String recipeName;   // может быть null (если рецепт удалён и recipeName пусто)
    private final double weightInGrams;
    private final double calories;
    private final double proteins;
    private final double fats;
    private final double carbs;
    private final double servings;

    public FoodEntryDto(Long id, String recipeName, double weightInGrams,
                        double calories, double proteins, double fats, double carbs,double servings) {
        this.id = id;
        this.recipeName = recipeName;
        this.weightInGrams = Math.round(weightInGrams * 10.0) / 10.0;
        this.calories = Math.round(calories * 10.0) / 10.0;
        this.proteins = Math.round(proteins * 10.0) / 10.0;
        this.fats = Math.round(fats * 10.0) / 10.0;
        this.carbs = Math.round(carbs * 10.0) / 10.0;
        this.servings = Math.round(servings * 100.0) / 100.0;
    }

    public static FoodEntryDto fromEntity(FoodEntry entry) {
        String name = entry.getRecipeName();
        // Fallback: если recipeName пусто, но рецепт есть — берём имя оттуда
        if (name == null && entry.getRecipe() != null) {
            name = entry.getRecipe().getName();
        }
        // Если вообще ничего — "Без названия"
        if (name == null) {
            name = "Без названия";
        }

        // Считаем количество порций
        double servings = 0;
        if (entry.getRecipe() != null && entry.getRecipe().getServingSizeGrams() > 0) {
            servings = entry.getWeightInGrams() / entry.getRecipe().getServingSizeGrams();
        }

        return new FoodEntryDto(
                entry.getId(),
                name,
                entry.getWeightInGrams(),
                entry.getCalories().doubleValue(),
                entry.getProtein().doubleValue(),
                entry.getFat().doubleValue(),
                entry.getCarbs().doubleValue(),
                servings
        );
    }
}