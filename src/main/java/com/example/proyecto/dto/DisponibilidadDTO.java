package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.DiaSemana;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalTime;

@Data
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