package com.e.mealtracker.exception;

import com.e.mealtracker.dto.ApiError;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ApiError> handleRecipeNotFound(RecipeNotFoundException ex) {
        ApiError error = new ApiError("RECIPE_NOT_FOUND", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND); // 404 — логично: ресурса нет
    }

    @ExceptionHandler(InvalidPortionWeightException.class)
    public ResponseEntity<ApiError> handleInvalidPortionWeight(InvalidPortionWeightException ex) {
        ApiError error = new ApiError("INVALID_PORTION_WEIGHT", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST); // 400 — неверный ввод
    }

    // Общий запасной обработчик
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGenericException(Exception ex) {
        // В логах всё равно будет полный стек — это важно для отладки
        ApiError error = new ApiError("INTERNAL_ERROR", "Что-то пошло не так", Instant.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

