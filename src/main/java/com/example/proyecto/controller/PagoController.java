package com.example.proyecto.controller;

import com.example.proyecto.domain.service.PagoService;
import com.example.proyecto.dto.PagoDTO;
import com.example.proyecto.dto.PagoRequestDTO;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class PagoController {

    private final PagoService pagoService;

    @Autowired
    public PagoController(PagoService pagoService) {
        this.pagoService = pagoService;
    }

    @PostMapping("/pagos/{reservaId}")
    public ResponseEntity<PagoDTO> procesarPago(
            @PathVariable Long reservaId,
            @Valid @RequestBody PagoRequestDTO dto) {
        PagoDTO pago = pagoService.procesarPago(reservaId, dto);
        return new ResponseEntity<>(pago, HttpStatus.CREATED);
    }
}
