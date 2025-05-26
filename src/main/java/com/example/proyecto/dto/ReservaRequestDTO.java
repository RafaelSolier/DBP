package com.example.proyecto.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaRequestDTO {

    @NotNull
    private Long servicioId;
    @NotNull
    private Long clienteId;
    @NotNull
    private LocalDateTime fechaReserva;
}
