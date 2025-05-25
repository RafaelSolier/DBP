package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResenaRequestDto {
    @NotNull(message = "servicioId es obligatorio")
    private Long servicioId;

    @NotNull(message = "clienteId es obligatorio")
    private Long clienteId;

    @NotNull(message = "calificacion entre 1 y 5 es obligatoria")
    @Min(1)
    @Max(5)
    private Integer calificacion;

    @NotNull(message = "fecha es obligatoria")
    private LocalDateTime fecha;

    @NotBlank(message = "comentario es obligatorio")
    @Size(max = 1000)
    private String comentario;

    // Getters y setters
}