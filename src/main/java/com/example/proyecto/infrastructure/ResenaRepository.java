package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Resena;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResenaRepository extends JpaRepository<Resena, Long> {
    List<Resena> findByServicioId(Long servicioId);
    List<Resena> findByServicioIdAndClienteId(Long servicioId, Long clienteId);
    void deleteByClienteId(Long clienteId);
}

