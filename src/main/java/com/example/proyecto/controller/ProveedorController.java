package com.example.proyecto.controller;

import com.example.proyecto.domain.enums.EstadoReserva;
import com.example.proyecto.domain.service.DisponibilidadService;
import com.example.proyecto.domain.service.ProveedorService;
import com.example.proyecto.domain.service.ReservaService;
import com.example.proyecto.domain.service.ServicioService;
import com.example.proyecto.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ProveedorController {

    private final ProveedorService proveedorService;
    private final ServicioService servicioService;
    private final DisponibilidadService disponibilidadService;
    private final ReservaService reservaService;

    @PostMapping("/proveedores")
    public ResponseEntity<Void> crearProveedor(@Valid @RequestBody ProveedorRequestDto dto) {
        proveedorService.registrar(dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PostMapping("/proveedores/{id}/servicios")
    public ResponseEntity<Void> agregarServicio(@PathVariable Long id,
                                                @Valid @RequestBody ServicioRequestDto dto) {
        proveedorService.crearServicio(id, dto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @PutMapping("/servicios/{id}")
    public ResponseEntity<Void> actualizarServicio(@PathVariable Long id,
                                                   @Valid @RequestBody ServicioRequestDto dto) {
        servicioService.actualizarServicio(id, dto);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/servicios/{id}/horarios")
    public ResponseEntity<Void> establecerHorarios(@PathVariable Long id,
                                                   @Valid @RequestBody List<DisponibilidadDTO> horarios) {
        disponibilidadService.obtenerPorServicio(id)
                .forEach(d -> disponibilidadService.eliminarDisponibilidad(d.getId()));
        horarios.forEach(h -> disponibilidadService.crearDisponibilidad(id, h));
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/proveedores/{id}/reservas")
    public ResponseEntity<List<ReservaDTO>> obtenerReservas(@PathVariable Long id) {
        List<ReservaDTO> reservas = reservaService.obtenerReservasPorProveedorYEstados(
                id, List.of(EstadoReserva.PENDIENTE, EstadoReserva.ACEPTADA));
        return ResponseEntity.ok(reservas);
    }

    @PatchMapping("/reservas/{resId}/aceptar")
    public ResponseEntity<ReservaDTO> aceptarReserva(@PathVariable Long resId) {
        ReservaDTO reserva = reservaService.aceptarReserva(resId);
        return ResponseEntity.ok(reserva);
    }

    @PatchMapping("/reservas/{resId}/completar")
    public ResponseEntity<ReservaDTO> completarReserva(@PathVariable Long resId) {
        ReservaDTO reserva = reservaService.completarReserva(resId);
        return ResponseEntity.ok(reserva);
    }
}
