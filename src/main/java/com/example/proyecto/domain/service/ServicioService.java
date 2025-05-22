package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    public void actualizarServicio(Long servicioId, ServicioRequestDto dto) {
        Servicio existing = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + servicioId));
        existing.setNombre(dto.getNombre());
        existing.setDescripcion(dto.getDescripcion());
        existing.setPrecio(dto.getPrecio());
        Categorias categoriaDto = Categorias.valueOf(dto.getCategoria());   // Puede desencadenar un IllegalArgumentException si no coincide con alguno
        existing.setCategoria(categoriaDto);
        servicioRepository.save(existing);
    }
}


