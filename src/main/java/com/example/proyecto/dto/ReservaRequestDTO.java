package com.example.proyecto.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Data
@RequiredArgsConstructor
public class ReservaRequestDTO {

    @NotNull(message = "servicioId es obligatorio")
    private Long servicioId;

    // clienteId se inyecta desde el controller
//    @NotNull(message = "clienteId es obligatorio")
//    private Long clienteId;

    @NotNull(message = "fechaReserva es obligatoria")
    private LocalDateTime fechaReserva;

    @NotBlank(message = "direccion es obligatoria")
    private String direccion;

}
