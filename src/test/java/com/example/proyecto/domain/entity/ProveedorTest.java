package com.example.proyecto.domain.entity;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class ProveedorTest {
    @Test
    void gettersAndSetters_shouldWork() {
        Proveedor c = new Proveedor();
        User user = new User();
        c.setId(1L);
        c.setNombre("Ana");
        user.setEmail("ana@example.com");
        c.setTelefono("987654321");
        user.setPassword("secret");

        assertAll("Proveedor properties",
                () -> assertEquals(1L, c.getId()),
                () -> assertEquals("Ana", c.getNombre()),
                () -> assertEquals("ana@example.com", user.getEmail()),
                () -> assertEquals("987654321", c.getTelefono()),
                () -> assertEquals("secret", user.getPassword())
        );
    }
}
