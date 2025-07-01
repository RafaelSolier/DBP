package com.example.proyecto.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProveedorResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String telefono;
    private BigDecimal rating;
}