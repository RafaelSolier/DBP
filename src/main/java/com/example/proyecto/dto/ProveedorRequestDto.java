package com.example.proyecto.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProveedorRequestDto {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Email
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @Size(max = 2000)
    private String descripcion;
    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 12)
    private String telefono;

    // Getters y setters
}