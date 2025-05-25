package com.example.proyecto.exception;


import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private Map<String,Object> buildBody(HttpStatus status, String message) {
        Map<String,Object> body = new HashMap<>();
        body.put("timestamp", Instant.now());
        body.put("status", status.value());
        body.put("error", status.getReasonPhrase());
        body.put("message", message);
        return body;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationErrors(MethodArgumentNotValidException ex) {
        // collect field errors
        Map<String,String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(err -> fieldErrors.put(err.getField(), err.getDefaultMessage()));
        Map<String,Object> body = buildBody(HttpStatus.BAD_REQUEST, "Validation failed");
        body.put("errors", fieldErrors);
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<?> handleValidationException(ValidationException ex) {
        return new ResponseEntity<>(buildBody(HttpStatus.BAD_REQUEST, ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({ IllegalArgumentException.class, ConflictException.class })
    public ResponseEntity<?> handleBadRequest(RuntimeException ex) {
        HttpStatus status = ex instanceof ConflictException
                ? HttpStatus.CONFLICT
                : HttpStatus.BAD_REQUEST;
        return new ResponseEntity<>(buildBody(status, ex.getMessage()), status);
    }

    @ExceptionHandler({ ResourceNotFoundException.class })
    public ResponseEntity<?> handleNotFound(ResourceNotFoundException ex) {
        return new ResponseEntity<>(buildBody(HttpStatus.NOT_FOUND, ex.getMessage()), HttpStatus.NOT_FOUND);
    }

//    @ExceptionHandler({ UnauthorizedException.class, BadCredentialsException.class, UsernameNotFoundException.class, JwtException.class })
//    public ResponseEntity<?> handleUnauthorized(RuntimeException ex) {
//        return new ResponseEntity<>(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()), HttpStatus.UNAUTHORIZED);
//    }
@ExceptionHandler({
        UnauthorizedException.class,
        BadCredentialsException.class,
        UsernameNotFoundException.class,
        JwtException.class,
        //InsufficientAuthenticationException.class   // ← lo añadimos aquí
})
public ResponseEntity<?> handleUnauthorized(Exception ex) {
    return new ResponseEntity<>(
            buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()),
            HttpStatus.UNAUTHORIZED
    );
}

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<?> handleForbidden(AccessDeniedException ex) {
        return new ResponseEntity<>(buildBody(HttpStatus.FORBIDDEN, ex.getMessage()), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleAll(Exception ex) {
        return new ResponseEntity<>(buildBody(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred"), HttpStatus.INTERNAL_SERVER_ERROR);
    }
//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<?> handleAll(Exception ex, HttpServletRequest request) {
//        // Log the exception for troubleshooting
//        log.error("Unhandled exception occurred", ex);
//        Map<String, Object> body = buildBody(HttpStatus.INTERNAL_SERVER_ERROR, ex.getMessage());
//        body.put("path", request.getRequestURI());
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(body);
//    }
//    @ExceptionHandler({ UnauthorizedException.class,
//            BadCredentialsException.class,
//            UsernameNotFoundException.class,
//            JwtException.class,
//            InsufficientAuthenticationException.class })
//    public ResponseEntity<?> handleUnauthorized(AuthenticationException ex) {
//        return ResponseEntity
//                .status(HttpStatus.UNAUTHORIZED)
//                .body(buildBody(HttpStatus.UNAUTHORIZED, ex.getMessage()));
//    }
}
