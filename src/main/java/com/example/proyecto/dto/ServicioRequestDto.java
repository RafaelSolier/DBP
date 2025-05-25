package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioRequestDto {
    @NotBlank(message = "El campo nombre no puede estar vacío")
    private String nombre;

    @NotBlank(message = "El campo descripcion no puede estar vacío")
    @Size(max = 2000)
    private String descripcion;

    @NotNull(message = "El campo precio no puede estar vacío")
    private BigDecimal precio;

    @NotBlank(message = "El campo categoria no puede estar vacío")
    private String categoria;

    // Getters y setters
}
