package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.enums.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);
    List<Reserva> findByServicioProveedorIdAndEstadoIn(Long proveedorId, List<EstadoReserva> estado);
    void deleteByClienteId(Long clienteId);

    List<Reserva> getByServicioId(Long servicioId);
}
