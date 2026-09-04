package com.e.mealtracker.exception;

public class RecipeNotFoundException extends RuntimeException {
    public RecipeNotFoundException(Long id) {
        super("Рецепт с ID " + id + " не найден");
    }
}
