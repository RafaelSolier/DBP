package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.entity.Reserva;
import com.example.proyecto.domain.entity.Resena;
import com.example.proyecto.domain.enums.Role;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ClienteTest {

    @Test
    void defaultFechaRegistroIsNow() {
        Cliente c = new Cliente();
        LocalDateTime before = LocalDateTime.now().minus(1, ChronoUnit.SECONDS);
        LocalDateTime after = LocalDateTime.now().plus(1, ChronoUnit.SECONDS);

        assertThat(c.getFechaRegistro())
                .as("fechaRegistro debe inicializarse al momento de instanciación")
                .isBetween(before, after);
    }

    @Test
    void gettersAndSettersWork() {
        Cliente c = new Cliente();
        c.setId(42L);
        c.setNombre("Juan");
        c.setApellido("Gómez");
        c.setTelefono("999123456");
        c.setFoto("avatar.png");
        LocalDateTime dt = LocalDateTime.of(2025, 5, 25, 12, 0);
        c.setFechaRegistro(dt);

        List<Reserva> reservas = new ArrayList<>();
        List<Resena> resenas = new ArrayList<>();
        c.setReservas(reservas);
        c.setResenas(resenas);

        User u = new User();
        u.setId(7L);
        u.setEmail("user@example.com");
        u.setPassword("pwd");
        u.getRoles().add(Role.ROLE_CLIENTE);
        c.setUser(u);

        assertThat(c.getId()).isEqualTo(42L);
        assertThat(c.getNombre()).isEqualTo("Juan");
        assertThat(c.getApellido()).isEqualTo("Gómez");
        assertThat(c.getTelefono()).isEqualTo("999123456");
        assertThat(c.getFoto()).isEqualTo("avatar.png");
        assertThat(c.getFechaRegistro()).isEqualTo(dt);
        assertThat(c.getReservas()).isSameAs(reservas);
        assertThat(c.getResenas()).isSameAs(resenas);
        assertThat(c.getUser()).isSameAs(u);
    }

    @Test
    void equalsAndHashCode_considerAllFields() {
        LocalDateTime dt = LocalDateTime.of(2025, 1, 1, 0, 0);
        List<Reserva> reservas = new ArrayList<>();
        List<Resena> resenas = new ArrayList<>();
        User u = new User();
        u.setId(7L);
        u.setEmail("user@example.com");
        u.setPassword("pwd");
        u.getRoles().add(Role.ROLE_CLIENTE);

        Cliente c1 = new Cliente();
        c1.setId(1L);
        c1.setNombre("A");
        c1.setApellido("B");
        c1.setTelefono("C");
        c1.setFoto("pic");
        c1.setFechaRegistro(dt);
        c1.setReservas(reservas);
        c1.setResenas(resenas);
        c1.setUser(u);

        Cliente c2 = new Cliente();
        c2.setId(1L);
        c2.setNombre("A");
        c2.setApellido("B");
        c2.setTelefono("C");
        c2.setFoto("pic");
        c2.setFechaRegistro(dt);
        c2.setReservas(reservas);
        c2.setResenas(resenas);
        c2.setUser(u);

        assertThat(c1).isEqualTo(c2);
        assertThat(c1.hashCode()).isEqualTo(c2.hashCode());
    }

    @Test
    void toString_includesKeyFields() {
        Cliente c = new Cliente();
        c.setId(99L);
        c.setNombre("X");
        c.setApellido("Y");
        c.setTelefono("T");
        c.setFoto("F");

        String s = c.toString();
        assertThat(s)
                .contains("id=99")
                .contains("nombre=X")
                .contains("apellido=Y")
                .contains("telefono=T")
                .contains("foto=F");
    }
}