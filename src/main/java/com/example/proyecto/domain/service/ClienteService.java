package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.dto.*;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final ServicioService servicioService;
    private final ReservaService reservaService;
    private final ReservaRepository reservaRepository;

    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado "+ id));
    }


    public List<ServicioDTO> buscarServicios(FiltroServicioDTO filtros) {
        return servicioService.buscarServicios(filtros);
    }


    public void cancelarReserva(Long clienteId, Long reservaId) {
        reservaService.cancelarReserva(clienteId, reservaId);
    }

    public List<ReservaDTO> misReservas(Long clienteId) {
        // 1) Verificar existencia del cliente
        clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado"));

        // 2) Si existe, delegar a ReservaService
        return reservaService.misReservas(clienteId);
    }

}
