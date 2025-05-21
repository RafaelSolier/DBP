package com.example.proyecto.dto;

import com.example.proyecto.domain.enumerates.DiaSemana;
import jakarta.validation.constraints.*;

import java.time.LocalTime;

public class DisponibilidadDTO {
    private Long id;

    @NotNull
    private DiaSemana diaSemana;

    @NotNull
    private LocalTime horaInicio;

    @NotNull
    private LocalTime horaFin;

    // Getters y setters
}
