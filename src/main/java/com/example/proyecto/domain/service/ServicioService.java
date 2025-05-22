// proyecto/src/main/java/com/example/proyecto/domain/service/ServicioService.java
package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.domain.enumerates.Categorias;
import com.example.proyecto.dto.FiltroServicioDTO;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.infrastructure.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ServicioService(ServicioRepository servicioRepository,
                           ModelMapper modelMapper) {
        this.servicioRepository = servicioRepository;
        this.modelMapper = modelMapper;
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
                Sort.by("tarifa").ascending() // o cualquier orden por defecto
        );

        // 3. Ejecutar consulta paginada
        Page<Servicio> page = servicioRepository.findAll(spec, pageable);

        // 4. Mapear a DTO y devolver la lista
        return page.getContent().stream()
                .map(srv -> modelMapper.map(srv, ServicioDTO.class))
                .collect(Collectors.toList());
    }
}
