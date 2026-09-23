package com.e.mealtracker.exception;

public class InvalidPortionWeightException extends RuntimeException {
    public InvalidPortionWeightException(double weight) {
        super("Вес порции должен быть больше 0 (получено: " + weight + ")");
    }
}
