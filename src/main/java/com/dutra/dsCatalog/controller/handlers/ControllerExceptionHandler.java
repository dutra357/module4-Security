package com.dutra.dsCatalog.controller.handlers;

import com.dutra.dsCatalog.dtos.exceptions.CustomError;
import com.dutra.dsCatalog.dtos.exceptions.ValidationError;
import com.dutra.dsCatalog.services.exceptions.DataBaseException;
import com.dutra.dsCatalog.services.exceptions.ResourceNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<CustomError> entityNotFoundException(ResourceNotFoundException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.NOT_FOUND;
        CustomError customError = new CustomError(Instant.now(), status.value(), "Resource not found.", exception.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(customError);
    }

    @ExceptionHandler(DataBaseException.class)
    public ResponseEntity<CustomError> dataBaseException(DataBaseException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.CONFLICT;
        CustomError customError = new CustomError(Instant.now(), status.value(), "DataBase exception.", exception.getMessage(), request.getRequestURI());

        return ResponseEntity.status(status).body(customError);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ValidationError> handleMethodArgumentNotValid(MethodArgumentNotValidException exception, HttpServletRequest request) {
        HttpStatus status = HttpStatus.UNPROCESSABLE_ENTITY;

        ValidationError error = new ValidationError(Instant.now(), status.value(), "Validation exception.", exception.getMessage(), request.getRequestURI());

        for (FieldError err : exception.getBindingResult().getFieldErrors()) {
            error.addError(err.getField(), err.getDefaultMessage());
        }

        return ResponseEntity.status(status).body(error);
    }
}
