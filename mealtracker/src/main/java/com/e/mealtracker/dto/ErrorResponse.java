package com.e.mealtracker.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private List<FieldError> errors; // для валидации по полям
    private String path;

    public static class FieldError {
        private String field;
        private String message;

        public FieldError(String field, String message) {
            this.field = field;
            this.message = message;
        }
    }

    // Конструкторы для разных случаев
    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public ErrorResponse(int status, String error, String message, List<FieldError> errors, String path) {
        this(status, error, message, path);
        this.errors = errors;
    }
}
