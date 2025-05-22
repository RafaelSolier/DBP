package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByProveedorIdAndEstado(Long proveedorId, EstadoReserva estado);
    List<Reserva> findByServicioProveedorId(Long proveedorId);
}
