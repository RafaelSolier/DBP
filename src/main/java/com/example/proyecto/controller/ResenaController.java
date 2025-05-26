package com.example.proyecto.controller;

import com.example.proyecto.domain.service.ResenaService;
import com.example.proyecto.dto.ResenaDTO;
import com.example.proyecto.dto.ResenaRequestDto;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ResenaController {

    private final ResenaService resenaService;

    @PostMapping("/resenas")
    public ResponseEntity<ResenaDTO> crearResena(@Valid @RequestBody ResenaRequestDto request) {
        ResenaDTO dto = resenaService.crearResena(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(dto);
    }

    /**
     * Obtiene todas las reseñas asociadas a un servicio.
     * @param servicioId ID del servicio
     * @return Lista de ResenaDTO
     */
    @GetMapping("/servicios/{servicioId}/resenas")
    public ResponseEntity<List<ResenaDTO>> obtenerResenasPorServicio(@PathVariable Long servicioId) {
        List<ResenaDTO> lista = resenaService.obtenerResenasPorServicio(servicioId);
        return ResponseEntity.ok(lista);
    }
}