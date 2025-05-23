package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.*;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.dto.FiltroServicioDTO;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    public Servicio findById(Long id) {
        return servicioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servicio "+ id));
    }
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

    public List<ServicioDTO> buscarServicios(FiltroServicioDTO filtros) {
        // 1. Construir Specification dinámico
        Specification<Servicio> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> preds = new ArrayList<>();

            if (filtros.getCategoria() != null && !filtros.getCategoria().isBlank()) {
                // Convertir cadena a enum, e.g. "LIMPIEZA"
                preds.add(cb.equal(
                        root.get("categoria"),
                        Categorias.valueOf(filtros.getCategoria())
                ));
            }
            if (filtros.getDireccion() != null && !filtros.getDireccion().isBlank()) {
                preds.add(cb.like(
                        cb.lower(root.get("direccion")),
                        "%" + filtros.getDireccion().toLowerCase() + "%"
                ));
            }
            if (filtros.getPrecioMin() != null) {
                preds.add(cb.ge(root.get("tarifa"), filtros.getPrecioMin()));
            }
            if (filtros.getPrecioMax() != null) {
                preds.add(cb.le(root.get("tarifa"), filtros.getPrecioMax()));
            }
            if (filtros.getCalificacionMin() != null) {
                preds.add(cb.ge(root.get("rating"), filtros.getCalificacionMin()));
            }
            return cb.and(preds.toArray(new Predicate[0]));
        };

        // 2. Preparar paginación
        Pageable pageable = PageRequest.of(
                filtros.getPage(),
                filtros.getSize(),
                Sort.by("id").ascending() // o cualquier orden por defecto
        );

        // 3. Ejecutar consulta paginada
        Page<Servicio> page = servicioRepository.findAll(spec, pageable);

        // 4. Mapear a DTO y devolver la lista
        return page.getContent().stream()
                .map(srv -> modelMapper.map(srv, ServicioDTO.class))
                .collect(Collectors.toList());
    }
}
