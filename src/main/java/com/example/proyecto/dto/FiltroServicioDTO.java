package com.example.proyecto.dto;

import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FiltroServicioDTO {
    private String categoria;
    private String direccion;
    @Min(0)
    private Double precioMin;
    @Min(0)
    private Double precioMax;
    @Min(0)
    private Double calificacionMin;
    @Min(0)
    private Integer page = 0;
    @Min(1)
    private Integer size = 10;
}