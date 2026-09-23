package com.e.mealtracker.exception;

public class IncompleteIngredientDataException extends RuntimeException {
    public IncompleteIngredientDataException(String message) {
        super(message);
    }
}