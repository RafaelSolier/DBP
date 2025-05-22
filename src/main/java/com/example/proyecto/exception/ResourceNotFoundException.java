package com.example.proyecto.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String entity, String field, Object value) {
        super(String.format("%s no encontrado con %s : '%s'", entity, field, value));
    }
}