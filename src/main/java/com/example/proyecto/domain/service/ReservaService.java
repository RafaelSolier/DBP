package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.domain.enums.EstadoReserva;
import com.example.proyecto.dto.ReservaDTO;
import com.example.proyecto.dto.ReservaRequestDTO;
import com.example.proyecto.email.events.*;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioService servicioService;
    private final ModelMapper modelMapper;
    private final ServicioRepository servicioRepository;
    private final ApplicationEventPublisher eventPublisher;

    public List<ReservaDTO> obtenerReservasPorProveedorYEstados(Long proveedorId, List<EstadoReserva> estados) {
        List<Reserva> reservas = reservaRepository.findByServicioProveedorIdAndEstadoIn(proveedorId, estados);
        return reservas.stream()
                .map(r -> modelMapper.map(r, ReservaDTO.class))
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> obtenerReservasPorClienteId(Long clienteId) {
        List<Reserva> reservas = reservaRepository.findByClienteId(clienteId);
        return reservas.stream()
                .map(r -> modelMapper.map(r, ReservaDTO.class))
                .collect(Collectors.toList());
    }

    public ReservaDTO aceptarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + reservaId));
        reserva.setEstado(EstadoReserva.ACEPTADA);
        Reserva updated = reservaRepository.save(reserva);
        eventPublisher.publishEvent(new AcceptReservaEvent(this, updated.getCliente().getNombre(),
                updated.getCliente().getUser().getEmail(),
                updated.getServicio().getProveedor().getNombre(),
                updated.getFechaReserva(),
                updated.getServicio().getNombre()));
        return modelMapper.map(updated, ReservaDTO.class);
    }

    public ReservaDTO completarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + reservaId));
        reserva.setEstado(EstadoReserva.COMPLETADA);
        Reserva updated = reservaRepository.save(reserva);
        // Enviar correo a cliente
        eventPublisher.publishEvent(new CompleteReservaEvent(this, reserva.getCliente().getNombre(),
                reserva.getCliente().getUser().getEmail(),
                reserva.getServicio().getProveedor().getUser().getEmail(),
                reserva.getFechaReserva(),
                reserva.getServicio().getNombre()));
        return modelMapper.map(updated, ReservaDTO.class);
    }

    public ReservaDTO crearReserva(Long clienteId,ReservaRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));
        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado"));
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setServicio(servicio);
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setDireccion(dto.getDireccion());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reservaRepository.save(reserva);
        // Enviar correo al cliente al solicitar una reserva (pero aún no hace el pago)
        eventPublisher.publishEvent(new CreateReservaEvent(this, reserva.getCliente().getNombre(),
                reserva.getServicio().getProveedor().getUser().getEmail(),
                reserva.getServicio().getProveedor().getNombre(),
                reserva.getFechaReserva(),
                reserva.getDireccion(),
                reserva.getServicio().getNombre()));
        return modelMapper.map(reserva, ReservaDTO.class);
    }

    @Transactional
    public void cancelarReserva(Long userId, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada "+ reservaId));
        if (!reserva.getCliente().getId().equals(userId)) {
            throw new ConflictException("No autorizado para cancelar esta reserva");
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ConflictException("Solo reservas pendientes pueden cancelarse");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);

        // Enviar email
        if (reserva.getCliente().getId().equals(userId)) {
            eventPublisher.publishEvent(new CancelReservaEvent(this,reserva.getCliente().getNombre(),
                    reserva.getServicio().getProveedor().getUser().getEmail(),
                    reserva.getServicio().getProveedor().getNombre(),
                    reserva.getFechaReserva(),
                    reserva.getServicio().getNombre()
            ));
        }
        if (reserva.getServicio().getProveedor().getId().equals(userId)){
            eventPublisher.publishEvent(new RejectReservaEvent(this, reserva.getCliente().getNombre(),
                    reserva.getCliente().getUser().getEmail(),
                    reserva.getServicio().getProveedor().getNombre(),
                    reserva.getFechaReserva(),
                    reserva.getServicio().getNombre()
            ));
        }
    }

    public List<ReservaDTO> misReservas(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId).stream()
                .map(r -> modelMapper.map(r, ReservaDTO.class))
                .collect(Collectors.toList());
    }

    public List<ReservaDTO> listarTodas() {
        List<Reserva> reservas = reservaRepository.findAll();
        return reservas.stream()
                .map(reserva -> modelMapper.map(reserva, ReservaDTO.class))
                .collect(Collectors.toList());
    }
}
