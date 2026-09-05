
package com.e.mealtracker.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidRecipeDataException extends RuntimeException {
    public InvalidRecipeDataException(String message) {
        super(message);
    }
}

