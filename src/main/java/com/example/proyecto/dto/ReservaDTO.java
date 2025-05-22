package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.EstadoReserva;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaDTO {
    private Long id;
    private LocalDateTime fechaReserva;
    private String direccion;
    private EstadoReserva estado;
    private Long clienteId;
    private Long servicioId;
}