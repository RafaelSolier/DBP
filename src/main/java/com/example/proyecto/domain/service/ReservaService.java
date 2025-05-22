package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.enums.EstadoReserva;
import com.example.proyecto.dto.ReservaDTO;
import com.example.proyecto.infrastructure.ReservaRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservaService {

    private final ReservaRepository reservaRepository;
    private final ModelMapper modelMapper;

    public List<ReservaDTO> obtenerReservasPorProveedor(Long proveedorId) {
        List<Reserva> reservas = reservaRepository.findByServicioProveedorId(proveedorId);
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
        return modelMapper.map(updated, ReservaDTO.class);
    }

    @Transactional
    public ReservaDTO completarReserva(Long reservaId) {
        Reserva reserva = reservaRepository.findById(reservaId)
                .orElseThrow(() -> new ResourceNotFoundException("Reserva no encontrada: " + reservaId));
        reserva.setEstado(EstadoReserva.COMPLETADA);
        Reserva updated = reservaRepository.save(reserva);
        return modelMapper.map(updated, ReservaDTO.class);
    }
}
