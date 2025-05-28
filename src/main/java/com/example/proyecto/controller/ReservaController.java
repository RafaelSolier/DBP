package com.example.proyecto.controller;

import com.example.proyecto.domain.enums.EstadoReserva;
import com.example.proyecto.domain.service.ReservaService;
import com.example.proyecto.dto.ReservaDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")            // o bien "/api/reservas"
@RequiredArgsConstructor
public class ReservaController {
    private final ReservaService reservaService;

    @GetMapping("/reservas")
    public ResponseEntity<List<ReservaDTO>> listarTodas() {
        List<ReservaDTO> todas = reservaService.listarTodas();
        return ResponseEntity.ok(todas);
    }
}
