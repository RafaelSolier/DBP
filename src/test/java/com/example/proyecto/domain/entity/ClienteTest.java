package com.example.proyecto.domain.entity;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ClienteTest {

    @Test
    void gettersAndSetters_shouldWork() {
        Cliente c = new Cliente();
        c.setId(1L);
        c.setNombre("Ana");
        c.setApellido("García");
        c.setEmail("ana@example.com");
        c.setTelefono("987654321");
        c.setPassword("secret");

        assertAll("Cliente properties",
                () -> assertEquals(1L, c.getId()),
                () -> assertEquals("Ana", c.getNombre()),
                () -> assertEquals("García", c.getApellido()),
                () -> assertEquals("ana@example.com", c.getEmail()),
                () -> assertEquals("987654321", c.getTelefono()),
                () -> assertEquals("secret", c.getPassword())
        );
    }
}