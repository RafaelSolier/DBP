package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProveedorRequestDto {
    @NotBlank
    private String nombre;

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String password;

    @Size(max = 2000)
    private String descripcion;

    @Size(max = 100)
    private String telefono;

    // Getters y setters
}