package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Disponibilidad;
import com.example.proyecto.domain.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DisponibilidadRepository extends JpaRepository<Disponibilidad, Long> {
    List<Disponibilidad> findByServicio(Servicio servicio);
}
