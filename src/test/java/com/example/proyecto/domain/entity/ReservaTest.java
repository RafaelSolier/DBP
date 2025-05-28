package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.EstadoReserva;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ReservaTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Reserva r = new Reserva();
        r.setId(100L);
        LocalDateTime fecha = LocalDateTime.now();
        r.setFechaReserva(fecha);
        r.setEstado(EstadoReserva.PENDIENTE);

        Cliente c = new Cliente(); c.setId(101L);
        Servicio s = new Servicio(); s.setId(102L);
        r.setCliente(c);
        r.setServicio(s);

        assertAll("Reserva props",
                () -> assertEquals(100L, r.getId()),
                () -> assertEquals(fecha, r.getFechaReserva()),
                () -> assertEquals(EstadoReserva.PENDIENTE, r.getEstado()),
                () -> assertEquals(101L, r.getCliente().getId()),
                () -> assertEquals(102L, r.getServicio().getId())
        );
    }
}
