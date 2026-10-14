package com.e.mealtracker.exception;

import com.e.mealtracker.domain.MealType;

import java.util.List;

public class NotEnoughRecipesException extends RuntimeException {

    private final List<MealType> missing;

    public NotEnoughRecipesException(List<MealType> missing) {
        super("Не хватает рецептов для: " + missing);
        this.missing = missing;
    }

    public List<MealType> getMissing() {
        return missing;
    }
}
