package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.domain.enumerates.EstadoReserva;
import com.example.proyecto.dto.ReservaDTO;
import com.example.proyecto.dto.ReservaRequestDTO;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ReservaService {
    private final ReservaRepository reservaRepository;
    private final ClienteRepository clienteRepository;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ReservaService(ReservaRepository reservaRepository,
                          ClienteRepository clienteRepository,
                          ServicioRepository servicioRepository,
                          ModelMapper modelMapper) {
        this.reservaRepository = reservaRepository;
        this.clienteRepository = clienteRepository;
        this.servicioRepository = servicioRepository;
        this.modelMapper = modelMapper;
    }

    @Transactional
    public ReservaDTO crearReserva(ReservaRequestDTO dto) {
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente", "id", dto.getClienteId()));
        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio", "id", dto.getServicioId()));
        Reserva reserva = new Reserva();
        reserva.setCliente(cliente);
        reserva.setServicio(servicio);
        reserva.setFechaReserva(dto.getFechaReserva());
        reserva.setEstado(EstadoReserva.PENDIENTE);
        Reserva saved = reservaRepository.save(reserva);
        return modelMapper.map(saved, ReservaDTO.class);
    }

    @Transactional
    public void cancelarReserva(Long clienteId, Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva", "id", reservaId));
        if (!reserva.getCliente().getId().equals(clienteId)) {
            throw new ConflictException("No autorizado para cancelar esta reserva");
        }
        if (reserva.getEstado() != EstadoReserva.PENDIENTE) {
            throw new ConflictException("Solo reservas pendientes pueden cancelarse");
        }
        reserva.setEstado(EstadoReserva.CANCELADA);
        reservaRepository.save(reserva);
    }

    public List<ReservaDTO> misReservas(Long clienteId) {
        return reservaRepository.findByClienteId(clienteId).stream()
                .map(r -> modelMapper.map(r, ReservaDTO.class))
                .collect(Collectors.toList());
    }
}
