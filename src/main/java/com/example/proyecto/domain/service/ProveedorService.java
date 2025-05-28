package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.dto.ProveedorRequestDto;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;


    public List<ServicioDTO> crearServicio(Long proveedorId, ServicioRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + proveedorId));
        Servicio servicio = modelMapper.map(dto, Servicio.class);
        servicio.setProveedor(proveedor);
        Servicio savedServicio = servicioRepository.save(servicio);
        return List.of(modelMapper.map(savedServicio, ServicioDTO.class));
    }
}
