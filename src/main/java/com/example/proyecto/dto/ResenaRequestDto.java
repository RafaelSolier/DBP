package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

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

    @NotBlank
    @Size(max = 1000)
    private String comentario;

    // Getters y setters
}