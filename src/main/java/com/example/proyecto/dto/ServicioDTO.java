package com.example.proyecto.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean activo;
    private String categoria;
    private Long proveedorId;
}
