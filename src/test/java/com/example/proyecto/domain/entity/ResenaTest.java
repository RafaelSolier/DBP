package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.DiaSemana;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ResenaTest {
    @Test
    void gettersAndSetters_shouldWork() {
        Resena resena = new Resena();
        Servicio servicio = new Servicio();
        Cliente cliente = new Cliente();
        LocalDateTime fecha = LocalDateTime.now();

        resena.setId(1L);
        resena.setCalificacion(5);
        resena.setComentario("Excelente servicio");
        resena.setFecha(fecha);
        resena.setServicio(servicio);
        resena.setCliente(cliente);

        assertAll(
                () -> assertEquals(1L, resena.getId()),
                () -> assertEquals(5, resena.getCalificacion()),
                () -> assertEquals("Excelente servicio", resena.getComentario()),
                () -> assertEquals(fecha, resena.getFecha()),
                () -> assertEquals(servicio, resena.getServicio()),
                () -> assertEquals(cliente, resena.getCliente())
        );
    }
}
