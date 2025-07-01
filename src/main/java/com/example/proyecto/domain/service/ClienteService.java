package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Resena;
import com.example.proyecto.dto.*;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ServicioService servicioService;
    private final ReservaService reservaService;
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final ResenaRepository resenaRepository;
    private final UserRepository userRepository;

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

    @Transactional
    public ClienteResponseDto actualizarCliente(Long clienteId, ClienteUpdateDTO dto) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteId));

        // Actualizar campos
        cliente.setNombre(dto.getNombre());
        cliente.setApellido(dto.getApellido());
        cliente.setTelefono(dto.getTelefono());
        if (dto.getFoto() != null) {
            cliente.setFoto(dto.getFoto());
        }

        Cliente clienteActualizado = clienteRepository.save(cliente);
        return clienteActualizado.toResponseDto();
    }

    @Transactional
    public void eliminarCliente(Long clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteId));

        // Eliminar pagos, reservas y reseñas asociadas al cliente
        cliente.getReservas().forEach(reserva -> {
            if (reserva.getPago() != null) {

                pagoRepository.delete(reserva.getPago());
            }
            reservaRepository.delete(reserva);
        });
            cliente.getResenas().forEach(resena -> resenaRepository.delete(resena));

        // Finalmente, eliminar el cliente
        clienteRepository.delete(cliente);

        // Eliminar usuario asociado al cliente
        userRepository.delete(cliente.getUser());
    }

}
