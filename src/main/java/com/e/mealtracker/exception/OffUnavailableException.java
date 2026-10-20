package com.e.mealtracker.exception;

public class OffUnavailableException extends RuntimeException {
    public OffUnavailableException(String message, Throwable cause) {
        super(message, cause);
    }
}
