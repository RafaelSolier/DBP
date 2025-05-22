package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Disponibilidad;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.dto.DisponibilidadDTO;
import com.example.proyecto.infrastructure.DisponibilidadRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class DisponibilidadService {

    private final DisponibilidadRepository disponibilidadRepository;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    /**
     * Obtiene todas las disponibilidades de un servicio.
     * @param servicioId ID del servicio
     * @return lista de DTOs de disponibilidad
     */
    public List<DisponibilidadDTO> obtenerPorServicio(Long servicioId) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + servicioId));
        List<Disponibilidad> list = disponibilidadRepository.findByServicio(servicio);
        return list.stream()
                .map(d -> modelMapper.map(d, DisponibilidadDTO.class))
                .collect(Collectors.toList());
    }

    /**
     * Crea una nueva disponibilidad para un servicio.
     * @param servicioId ID del servicio
     * @param dto DTO con datos de disponibilidad
     * @return DTO creado con ID asignado
     */
    public DisponibilidadDTO crearDisponibilidad(Long servicioId, DisponibilidadDTO dto) {
        Servicio servicio = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + servicioId));
        Disponibilidad disp = modelMapper.map(dto, Disponibilidad.class);
        disp.setServicio(servicio);
        Disponibilidad saved = disponibilidadRepository.save(disp);
        return modelMapper.map(saved, DisponibilidadDTO.class);
    }

    /**
     * Actualiza una disponibilidad existente.
     * @param id ID de la disponibilidad
     * @param dto DTO con datos actualizados
     * @return DTO actualizado
     */
    public DisponibilidadDTO actualizarDisponibilidad(Long id, DisponibilidadDTO dto) {
        Disponibilidad existing = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada: " + id));
        // Actualizar campos
        existing.setDiaSemana(dto.getDiaSemana());
        existing.setHoraInicio(dto.getHoraInicio());
        existing.setHoraFin(dto.getHoraFin());
        Disponibilidad updated = disponibilidadRepository.save(existing);
        return modelMapper.map(updated, DisponibilidadDTO.class);
    }

    /**
     * Elimina una disponibilidad por ID.
     * @param id ID de la disponibilidad
     */
    public void eliminarDisponibilidad(Long id) {
        Disponibilidad existing = disponibilidadRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilidad no encontrada: " + id));
        disponibilidadRepository.delete(existing);
    }
}

