package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioRequestDto {
    @NotBlank
    private String nombre;

    @NotBlank
    @Size(max = 2000)
    private String descripcion;

    @NotNull
    private BigDecimal precio;

    @NotBlank
    private String categoria;

    // Getters y setters
}
