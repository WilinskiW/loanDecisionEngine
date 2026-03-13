package com.task.decisionengine.infrastructure;

import com.task.decisionengine.infrastructure.dto.ValidationErrorResponseDto;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String DEFAULT_VALIDATION_MESSAGE = "Validation failed";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationErrorResponseDto> handleValidationException(MethodArgumentNotValidException ex) {
        var fieldError = ex.getBindingResult().getFieldError();
        var message = fieldError != null ? fieldError.getDefaultMessage() : DEFAULT_VALIDATION_MESSAGE;

        return ResponseEntity.badRequest()
                .body(new ValidationErrorResponseDto(HttpStatus.BAD_REQUEST.value(), message));
    }
}
