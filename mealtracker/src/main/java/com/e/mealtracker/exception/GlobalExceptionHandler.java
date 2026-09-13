package com.e.mealtracker.exception;

import com.e.mealtracker.dto.ApiError;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.Instant;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // ================== 400 BAD REQUEST ==================

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex,
                                                     HttpServletRequest request) {
        StringBuilder messages = new StringBuilder();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if (messages.length() > 0) messages.append("; ");
            messages.append(error.getField()).append(": ").append(error.getDefaultMessage());
        }
        return build(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR", messages.toString(), request);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleNotReadable(HttpMessageNotReadableException ex,
                                                      HttpServletRequest request) {
        log.warn("Malformed JSON at {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "MALFORMED_JSON",
                "Тело запроса содержит некорректный JSON", request);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                       HttpServletRequest request) {
        String msg = String.format("Параметр '%s' имеет неверный тип: %s",
                ex.getName(), ex.getValue());
        return build(HttpStatus.BAD_REQUEST, "TYPE_MISMATCH", msg, request);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "BAD_REQUEST", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidPortionWeightException.class)
    public ResponseEntity<ApiError> handleInvalidPortionWeight(InvalidPortionWeightException ex,
                                                               HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_PORTION_WEIGHT", ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRecipeDataException.class)
    public ResponseEntity<ApiError> handleInvalidRecipeData(InvalidRecipeDataException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INVALID_RECIPE_DATA", ex.getMessage(), request);
    }

    @ExceptionHandler(IncompleteIngredientDataException.class)
    public ResponseEntity<ApiError> handleIncompleteIngredientData(IncompleteIngredientDataException ex,
                                                                   HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, "INCOMPLETE_INGREDIENT_DATA", ex.getMessage(), request);
    }

    // ================== 401 UNAUTHORIZED ==================

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiError> handleBadCredentials(BadCredentialsException ex,
                                                         HttpServletRequest request) {
        return build(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS",
                "Неверное имя пользователя или пароль", request);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiError> handleUsernameNotFound(UsernameNotFoundException ex,
                                                           HttpServletRequest request) {
        // Не раскрываем, существует ли пользователь
        return build(HttpStatus.UNAUTHORIZED, "BAD_CREDENTIALS",
                "Неверное имя пользователя или пароль", request);
    }

    // ================== 403 FORBIDDEN ==================

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex,
                                                       HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, "ACCESS_DENIED",
                "Недостаточно прав для выполнения операции", request);
    }

    // ================== 404 NOT FOUND ==================

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(NotFoundException ex,
                                                   HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "NOT_FOUND", ex.getMessage(), request);
    }

    // ================== 409 CONFLICT ==================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrity(DataIntegrityViolationException ex,
                                                        HttpServletRequest request) {
        log.warn("Data integrity violation at {}: {}",
                request.getRequestURI(), ex.getMostSpecificCause().getMessage());
        return build(HttpStatus.CONFLICT, "DATA_INTEGRITY_VIOLATION",
                "Нарушение целостности данных (возможно, дубликат или связанные записи)", request);
    }

    // ================== 500 INTERNAL ==================

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest request) {
        log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR",
                "Произошла непредвиденная ошибка на сервере", request);
    }

    // ================== Helper ==================

    private ResponseEntity<ApiError> build(HttpStatus status, String code,
                                           String message, HttpServletRequest request) {
        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                code,
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(error);
    }
}


