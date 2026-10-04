package com.e.mealtracker.exception;

public class AccountDeletedException extends RuntimeException {
    public AccountDeletedException(String message) {
        super(message);
    }
}