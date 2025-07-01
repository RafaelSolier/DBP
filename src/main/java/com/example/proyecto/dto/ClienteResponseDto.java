package com.example.proyecto.dto;

import lombok.Data;

@Data
public class ClienteResponseDto {
    private Long id;
    private String nombre;
    private String apellido;
    private String telefono;

}
