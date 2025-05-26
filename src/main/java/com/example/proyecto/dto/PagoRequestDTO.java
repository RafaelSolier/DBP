package com.example.proyecto.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
@Data
public class PagoRequestDTO {
    @NotNull
    @DecimalMin("0.0")
    private BigDecimal monto;

    @NotBlank
    private String estado;
}
