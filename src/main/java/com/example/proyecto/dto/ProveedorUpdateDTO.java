package com.example.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ProveedorUpdateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @Size(max = 2000, message = "La descripción no puede exceder 2000 caracteres")
    private String descripcion;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;
}