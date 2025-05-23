package com.example.proyecto.controller;

import com.example.proyecto.domain.service.ClienteService;
import com.example.proyecto.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService clienteService;



    @PostMapping("/clientes")
    public ResponseEntity<ClienteDTO> registrar(@Valid @RequestBody ClienteRequestDTO dto) {
        ClienteDTO created = clienteService.registrar(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/clientes/login")
    public ResponseEntity<TokenDTO> login(@Valid @RequestBody LoginDTO dto) {
        TokenDTO token = clienteService.login(dto);
        return ResponseEntity.ok(token);
    }

    @GetMapping("/servicios")
    public ResponseEntity<List<ServicioDTO>> buscarServicios(@Valid FiltroServicioDTO filtros) {
        List<ServicioDTO> servicios = clienteService.buscarServicios(filtros);
        return ResponseEntity.ok(servicios);
    }

    @PostMapping("/clientes/{id}/reservas")
    public ResponseEntity<ReservaDTO> crearReserva(
            @PathVariable Long id,
            @Valid @RequestBody ReservaRequestDTO dto
    ) {
        dto.setClienteId(id);
        ReservaDTO reserva = clienteService.crearReserva(id, dto);
        return new ResponseEntity<>(reserva, HttpStatus.CREATED);
    }

    @PatchMapping("/clientes/{id}/reservas/{resId}/cancelar")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cancelarReserva(@PathVariable Long id,
                                @PathVariable Long resId) {
        clienteService.cancelarReserva(id, resId);
    }

    @GetMapping("/clientes/{id}/reservas")
    public ResponseEntity<List<ReservaDTO>> misReservas(@PathVariable Long id) {
        List<ReservaDTO> reservas = clienteService.misReservas(id);
        return ResponseEntity.ok(reservas);
    }
}