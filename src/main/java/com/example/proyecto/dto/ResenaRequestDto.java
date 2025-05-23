package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResenaRequestDto {
    @NotNull
    private Long servicioId;

    @NotNull
    private Long clienteId;

    @NotNull
    @Min(1)
    @Max(5)
    private Integer calificacion;

    @NotNull
    private LocalDateTime fecha;

    @NotBlank
    @Size(max = 1000)
    private String comentario;

    // Getters y setters
}