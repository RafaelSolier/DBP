package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.Categorias;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ServicioTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Servicio s = new Servicio();
        s.setId(10L);
        s.setCategoria(Categorias.LIMPIEZA);

        Proveedor p = new Proveedor();
        p.setId(20L);
        s.setProveedor(p);

        Disponibilidad d1 = new Disponibilidad(); d1.setId(30L);
        Disponibilidad d2 = new Disponibilidad(); d2.setId(31L);
        s.setDisponibilidades(Arrays.asList(d1, d2));

        Resena r = new Resena(); r.setId(40L);
        s.setResenas(List.of(r));

        assertAll("Servicio props",
                () -> assertEquals(10L, s.getId()),
                () -> assertEquals(Categorias.LIMPIEZA, s.getCategoria()),
                () -> assertEquals(20L, s.getProveedor().getId()),
                () -> assertEquals(2, s.getDisponibilidades().size()),
                () -> assertEquals(1, s.getResenas().size())
        );
    }
}