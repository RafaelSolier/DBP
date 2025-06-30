package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicio, Long>, JpaSpecificationExecutor<Servicio> {
    List<Servicio> findByActivoTrue();
    List<Servicio> findByProveedorIdAndActivoTrue(Long proveedorId);
    List<Servicio> findByProveedorId(Long proveedorId);
}
