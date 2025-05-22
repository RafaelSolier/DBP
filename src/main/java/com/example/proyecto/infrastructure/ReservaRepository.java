package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.enumerates.EstadoReserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReservaRepository extends JpaRepository<Reserva, Long> {
    List<Reserva> findByClienteId(Long clienteId);

}
