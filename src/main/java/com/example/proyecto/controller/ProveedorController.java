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
import org.springframework.security.access.prepost.PreAuthorize;
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


    @PostMapping("/proveedores/{id}/servicios")
    public ResponseEntity<ServicioDTO> agregarServicio(@PathVariable Long id,
                                                @Valid @RequestBody ServicioRequestDto dto) {
        ServicioDTO servicio = proveedorService.crearServicio(id, dto).get(0);
        return ResponseEntity.status(HttpStatus.CREATED).body(servicio);
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
                id, List.of(EstadoReserva.PENDIENTE, EstadoReserva.ACEPTADA,EstadoReserva.CANCELADA,EstadoReserva.COMPLETADA ));
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

    @PatchMapping("/servicios/{id}/estado")
    public ResponseEntity<ServicioDTO> cambiarEstado(
            @PathVariable Long id,
            @RequestParam boolean activo) {
        ServicioDTO updated = servicioService.cambiarEstado(id, activo);
        return ResponseEntity.ok(updated);
    }
    @GetMapping("/servicios/activos")
    public ResponseEntity<List<ServicioDTO>> listarServiciosActivos() {
        List<ServicioDTO> servicios = servicioService.listarServiciosActivos();
        return ResponseEntity.ok(servicios);
    }

    @GetMapping("/servicios/{proveedorId}/servicios")
    public ResponseEntity<List<ServicioDTO>> listarServiciosProveedor(
            @PathVariable Long proveedorId) {
        return ResponseEntity.ok(
                servicioService.listarServiciosPorProveedor(proveedorId)
        );
    }

    @DeleteMapping("/servicios/proveedor/{id}")
    public ResponseEntity<Void> eliminarServicio(@PathVariable Long id) {
        servicioService.eliminarServicio(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/proveedor/{id}")
    public ResponseEntity<Void> eliminarProveedor(@PathVariable Long id) {
        proveedorService.eliminarProveedor(id);
        return ResponseEntity.ok().build();
    }
}