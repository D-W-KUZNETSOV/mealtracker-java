package com.e.mealtracker.exception;

import com.e.mealtracker.dto.ApiError;
import com.e.mealtracker.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;
@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecipeNotFoundException.class)
    public ResponseEntity<ApiError> handleRecipeNotFound(RecipeNotFoundException ex) {
        ApiError error = new ApiError("RECIPE_NOT_FOUND", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(InvalidPortionWeightException.class)
    public ResponseEntity<ApiError> handleInvalidPortionWeight(InvalidPortionWeightException ex) {
        ApiError error = new ApiError("INVALID_PORTION_WEIGHT", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Добавляем обработку ошибок из StatsService
    @ExceptionHandler(InvalidRecipeDataException.class)
    public ResponseEntity<ApiError> handleInvalidRecipeData(InvalidRecipeDataException ex) {
        ApiError error = new ApiError("INVALID_RECIPE_DATA", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IncompleteIngredientDataException.class)
    public ResponseEntity<ApiError> handleIncompleteIngredientData(IncompleteIngredientDataException ex) {
        ApiError error = new ApiError("INCOMPLETE_INGREDIENT_DATA", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    // Обработка стандартной валидации (если используешь @Valid в контроллерах)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidationExceptions(MethodArgumentNotValidException ex) {
        StringBuilder messages = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if (messages.length() > 0) messages.append(", ");
            messages.append(error.getField()).append(": ").append(error.getDefaultMessage());
        }
        ApiError apiError = new ApiError("VALIDATION_ERROR", messages.toString(), Instant.now());
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException ex, HttpServletRequest request) {
        ErrorResponse response = new ErrorResponse(
                404,
                "Not Found",
                ex.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }


    // Глобальный "страховочный" хендлер для любых непредвиденных ошибок
    @ExceptionHandler(Exception.class)
   public ResponseEntity<ApiError> handleGenericException(Exception ex) {
       //  ✅ Логируем ошибку в консоль с полным стектрейсом
        log.error("Ошибка на сервере: ", ex);

        // В продакшене НИКОГДА не отдавай ex.getMessage() клиенту
        ApiError error = new ApiError("INTERNAL_SERVER_ERROR", "Произошла непредвиденная ошибка на сервере", Instant.now());
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        ApiError error = new ApiError("BAD_REQUEST", ex.getMessage(), Instant.now());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }
}


