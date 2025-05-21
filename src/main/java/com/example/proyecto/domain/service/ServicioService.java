package com.example.proyecto.domain.service;

import com.example.proyecto.dto.FiltroServicioDTO;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.infrastructure.ServicioRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ServicioService(ServicioRepository servicioRepository, ModelMapper modelMapper) {
        this.servicioRepository = servicioRepository;
        this.modelMapper = modelMapper;
    }

    public List<ServicioDTO> buscarServicios(FiltroServicioDTO filtros) {
        // TODO: implementar lógica de filtrado (categoría, dirección, precio, calificación)
        return servicioRepository.findAll().stream()
                .map(s -> modelMapper.map(s, ServicioDTO.class))
                .collect(Collectors.toList());
    }
}