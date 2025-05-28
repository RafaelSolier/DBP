package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.EstadoPago;
import org.junit.jupiter.api.Test;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.junit.jupiter.api.Assertions.*;

class PagoTest {
    @Test
    void gettersAndSetters() {
        Pago p = new Pago();
        p.setId(10L);
        p.setMonto(new BigDecimal("123.45"));
        LocalDateTime now = LocalDateTime.now();
        p.setFechaPago(now);
        p.setEstado(EstadoPago.COMPLETADO);
        Reserva r = new Reserva(); r.setId(201L);
        p.setReserva(r);

        assertAll(
                () -> assertEquals(10L, p.getId()),
                () -> assertEquals(new BigDecimal("123.45"), p.getMonto()),
                () -> assertEquals(now, p.getFechaPago()),
                () -> assertEquals(EstadoPago.COMPLETADO, p.getEstado()),
                () -> assertEquals(201L, p.getReserva().getId())
        );
    }
}