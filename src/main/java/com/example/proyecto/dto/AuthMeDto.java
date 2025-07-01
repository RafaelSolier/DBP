package com.example.proyecto.dto;

import lombok.Data;

import java.util.Set;

@Data
public class AuthMeDto {
    private Long id;
    private String nombre;
    private String email;
    private String telefono;
    private String descripcion;
    private Set<String> role;
}