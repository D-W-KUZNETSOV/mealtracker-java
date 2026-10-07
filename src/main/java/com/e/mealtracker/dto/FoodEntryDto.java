package com.e.mealtracker.dto;

import com.e.mealtracker.domain.FoodEntry;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class FoodEntryDto {

    private final Long id;
    private final String itemName;
    private final Long ingredientId;   // 🆕 null = рецепт (или запись без ингредиента)
    private final double weightInGrams;
    private final double calories;
    private final double proteins;
    private final double fats;
    private final double carbs;
    private final double servings;

    public FoodEntryDto(Long id, String itemName, Long ingredientId, double weightInGrams,
                        double calories, double proteins, double fats, double carbs, double servings) {
        this.id = id;
        this.itemName = itemName;
        this.ingredientId = ingredientId;
        this.weightInGrams = Math.round(weightInGrams * 10.0) / 10.0;
        this.calories = Math.round(calories * 10.0) / 10.0;
        this.proteins = Math.round(proteins * 10.0) / 10.0;
        this.fats = Math.round(fats * 10.0) / 10.0;
        this.carbs = Math.round(carbs * 10.0) / 10.0;
        this.servings = Math.round(servings * 100.0) / 100.0;
    }

    public static FoodEntryDto fromEntity(FoodEntry entry) {
        // 🆕 Теперь имя хранится в itemName (универсальное — и рецепт, и ингредиент)
        String name = entry.getItemName();

        // Fallback: если itemName пусто, но рецепт есть — берём имя оттуда
        if (name == null && entry.getRecipe() != null) {
            name = entry.getRecipe().getName();
        }
        // Fallback: если itemName пусто, но ингредиент есть — берём имя оттуда
        if (name == null && entry.getIngredient() != null) {
            name = entry.getIngredient().getName();
        }
        // Если вообще ничего — "Без названия"
        if (name == null) {
            name = "Без названия";
        }

        // 🆕 ingredientId — если запись про ингредиент
        Long ingredientId = entry.getIngredient() != null
                ? entry.getIngredient().getId()
                : null;

        // Считаем количество порций (только для рецептов)
        double servings = 0;
        if (entry.getRecipe() != null && entry.getRecipe().getServingSizeGrams() > 0) {
            servings = entry.getWeightInGrams() / entry.getRecipe().getServingSizeGrams();
        }

        return new FoodEntryDto(
                entry.getId(),
                name,
                ingredientId,
                entry.getWeightInGrams(),
                entry.getCalories().doubleValue(),
                entry.getProtein().doubleValue(),
                entry.getFat().doubleValue(),
                entry.getCarbs().doubleValue(),
                servings
        );
    }
}