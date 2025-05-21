package com.example.proyecto.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PagoDTO {
    private Long id;
    private BigDecimal monto;
    private LocalDateTime fechaPago;
    private String estado;
    private Long reservaId;
}
