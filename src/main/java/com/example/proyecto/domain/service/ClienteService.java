package com.example.proyecto.domain.service;

import com.example.proyecto.Security.JwtService;
import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.dto.*;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.exception.UnauthorizedException;
import com.example.proyecto.infrastructure.ClienteRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    //private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final ServicioService servicioService;
    private final ReservaService reservaService;

    public Cliente findById(Long id) {
        return clienteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado "+ id));
    }

    public ClienteDTO registrar(ClienteRequestDTO dto) {
        clienteRepository.findByEmail(dto.getEmail()).ifPresent(c -> {
            throw new ConflictException("El correo ya está registrado");
        });
        Cliente cliente = modelMapper.map(dto, Cliente.class);
        cliente.setPassword(passwordEncoder.encode(dto.getPassword()));
        Cliente saved = clienteRepository.save(cliente);
        return modelMapper.map(saved, ClienteDTO.class);
    }

    public TokenDTO login(LoginDTO dto) {
        Cliente cliente = clienteRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Credenciales inválidas"));
        if (!passwordEncoder.matches(dto.getPassword(), cliente.getPassword())) {
            throw new UnauthorizedException("Credenciales inválidas");
        }
        String token = jwtService.generateToken(cliente);
        return new TokenDTO(token);
    }

    public List<ServicioDTO> buscarServicios(FiltroServicioDTO filtros) {
        return servicioService.buscarServicios(filtros);
    }

    public ReservaDTO crearReserva(Long clienteId, ReservaRequestDTO dto) {
        dto.setClienteId(clienteId);
        return reservaService.crearReserva(dto);
    }

    public void cancelarReserva(Long clienteId, Long reservaId) {
        reservaService.cancelarReserva(clienteId, reservaId);
    }

    public List<ReservaDTO> misReservas(Long clienteId) {
        return reservaService.misReservas(clienteId);
    }
}
