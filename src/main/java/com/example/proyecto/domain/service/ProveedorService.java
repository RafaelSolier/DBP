package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.dto.ProveedorRequestDto;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Transactional
@RequiredArgsConstructor
public class ProveedorService {

    private final ProveedorRepository proveedorRepository;
    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;


    public void crearServicio(Long proveedorId, ServicioRequestDto dto) {
        Proveedor proveedor = proveedorRepository.findById(proveedorId)
                .orElseThrow(() -> new ResourceNotFoundException("Proveedor no encontrado: " + proveedorId));
        Servicio servicio = modelMapper.map(dto, Servicio.class);
        servicio.setProveedor(proveedor);
        servicioRepository.save(servicio);
    }
}
