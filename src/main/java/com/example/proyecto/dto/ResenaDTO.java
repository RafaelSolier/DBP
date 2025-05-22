package com.example.proyecto.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ResenaDTO {
    private Long id;
    private Long servicioId;
    private Long clienteId;
    private Integer calificacion;
    private String comentario;
    private LocalDateTime fecha;

    // Getters y setters
}
