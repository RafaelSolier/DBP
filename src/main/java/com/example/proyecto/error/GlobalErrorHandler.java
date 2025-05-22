package com.example.proyecto.error;


import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.exception.UnauthorizedException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice
public class GlobalErrorHandler {

    record ApiError(LocalDateTime timestamp, int status, String error, List<String> messages, String path) {}

    // Maneja validaciones de @Valid
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError fe : ex.getBindingResult().getFieldErrors()) {
            errors.add(fe.getField() + ": " + fe.getDefaultMessage());
        }
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.BAD_REQUEST.value(),
                "Validation Failed",
                errors,
                request.getDescription(false).replace("uri=","")
        );
        return new ResponseEntity<>(apiError, HttpStatus.BAD_REQUEST);
    }

    // ConflictException -> HTTP 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.CONFLICT.value(),
                "Conflict",
                List.of(ex.getMessage()),
                request.getDescription(false).replace("uri=","")
        );
        return new ResponseEntity<>(apiError, HttpStatus.CONFLICT);
    }

    // UnauthorizedException -> HTTP 401
    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiError> handleUnauthorized(UnauthorizedException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                List.of(ex.getMessage()),
                request.getDescription(false).replace("uri=","")
        );
        return new ResponseEntity<>(apiError, HttpStatus.UNAUTHORIZED);
    }

    // ResourceNotFoundException -> HTTP 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(ResourceNotFoundException ex, WebRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.NOT_FOUND.value(),
                "Not Found",
                List.of(ex.getMessage()),
                request.getDescription(false).replace("uri=","")
        );
        return new ResponseEntity<>(apiError, HttpStatus.NOT_FOUND);
    }

    // Captura cualquier otra excepción no controlada -> HTTP 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAll(Exception ex, WebRequest request) {
        ApiError apiError = new ApiError(
                LocalDateTime.now(),
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                List.of("Ocurrió un error inesperado"),
                request.getDescription(false).replace("uri=","")
        );
        // opcional: registrar stacktrace en logs
        ex.printStackTrace();
        return new ResponseEntity<>(apiError, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
