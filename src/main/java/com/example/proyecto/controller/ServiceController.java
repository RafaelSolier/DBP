package com.example.proyecto.controller;

import com.example.proyecto.domain.service.DisponibilidadService;
import com.example.proyecto.dto.DisponibilidadDTO;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ServiceController {
    private final DisponibilidadService disponibilidadService;
    @GetMapping("/servicios/{id}/horarios")
    public List<DisponibilidadDTO> obtenerHorarioServicio(@PathVariable Long id) {
        return disponibilidadService.obtenerHorarioServicio(id);
    }

}
