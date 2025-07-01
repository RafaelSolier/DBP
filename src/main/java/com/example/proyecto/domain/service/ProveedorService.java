package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.dto.ProveedorResponseDTO;
import com.example.proyecto.dto.ProveedorUpdateDTO;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.*;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;
    private final DisponibilidadRepository disponibilidadRepository;
    private final ResenaRepository resenaRepository;
    private final ReservaRepository reservaRepository;
    private final PagoRepository pagoRepository;
    private final UserRepository userRepository;

    public List<ServicioDTO> crearServicio(Long proveedorId, ServicioRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + proveedorId));
        Servicio servicio = modelMapper.map(dto, Servicio.class);
        servicio.setProveedor(proveedor);
        Servicio savedServicio = servicioRepository.save(servicio);
        return List.of(modelMapper.map(savedServicio, ServicioDTO.class));
    }

    @Transactional
    public ProveedorResponseDTO actualizarProveedor(Long proveedorId, ProveedorUpdateDTO dto) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + proveedorId));

        // Actualizar campos
        proveedor.setNombre(dto.getNombre());
        proveedor.setDescripcion(dto.getDescripcion());
        proveedor.setTelefono(dto.getTelefono());

        Proveedor proveedorActualizado = proveedorRepository.save(proveedor);
        return modelMapper.map(proveedorActualizado, ProveedorResponseDTO.class);
    }

    @Transactional
    public void eliminarProveedor(Long idProveedor) {
        Proveedor proveedor = proveedorRepository.findById(idProveedor)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + idProveedor));

        // Eliminar servicios
        if (proveedor.getServicios() != null) {
            proveedor.getServicios().forEach(servicio -> {
                // Eliminar disponibilidad
                servicio.getDisponibilidades().forEach(disponibilidad -> {
                    disponibilidadRepository.delete(disponibilidad);
                });

                // Eliminar Reseñas
                if (servicio.getResenas() != null) {
                    servicio.getResenas().forEach(resena -> {
                        resenaRepository.delete(resena);
                    });
                }
                ;

                // Eliminar reservas
                List<Reserva> reservas = reservaRepository.getByServicioId(servicio.getId());
                reservas.forEach(reserva -> {
                    // Eliminar Pagos
                    if (reserva.getPago() != null) {
                        pagoRepository.delete(reserva.getPago());
                    }
                    reservaRepository.delete(reserva);
                });

                // Eliminar servicios
                servicioRepository.delete(servicio);

            });
        }
        // Finalmente, eliminar el proveedor
        proveedorRepository.delete(proveedor);

        // Eliminar usuario asociado al proveedor
        userRepository.delete(proveedor.getUser());
    }
}
