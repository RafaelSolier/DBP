package com.example.proyecto.controller;

import com.example.proyecto.domain.service.ClienteService;
import com.example.proyecto.domain.service.ReservaService;
import com.example.proyecto.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;
    private final ReservaService reservaService;

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioDTO>> buscarServicios(@Valid FiltroServicioDTO filtros) {
        List<ServicioDTO> servicios = clienteService.buscarServicios(filtros);
        return ResponseEntity.ok(servicios);
    }


    @PostMapping("/clientes/{clienteId}/reservas")
    public ResponseEntity<ReservaDTO> crearReserva(
            @PathVariable Long clienteId,
            @Valid @RequestBody ReservaRequestDTO dto
    ) {
        ReservaDTO reserva = reservaService.crearReserva(clienteId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(reserva);
    }

    @PatchMapping("/clientes/{id}/reservas/{resId}/cancelar")
    public ResponseEntity<Void> cancelarReserva(@PathVariable Long id,
                                @PathVariable Long resId) {
        clienteService.cancelarReserva(id, resId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/clientes/{id}/reservas")
    public ResponseEntity<List<ReservaDTO>> misReservas(@PathVariable Long id) {
        List<ReservaDTO> reservas = clienteService.misReservas(id);
        return ResponseEntity.ok(reservas);
    }

    @GetMapping("/servicios/clientes/{id}")
    public ResponseEntity<ClienteResponseDto> obtenerCliente(@PathVariable Long id) {
        ClienteResponseDto cliente = clienteService.findById(id).toResponseDto();
        return ResponseEntity.ok(cliente);
    }

    @DeleteMapping("/clientes/{id}")
    public ResponseEntity<Void> eliminarCliente(@PathVariable Long id) {
        clienteService.eliminarCliente(id);

        return ResponseEntity.noContent().build();
    }
}