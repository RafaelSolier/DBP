package com.example.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ClienteUpdateDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El apellido es obligatorio")
    private String apellido;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 15, message = "El teléfono no puede exceder 15 caracteres")
    private String telefono;

    private String foto;
}