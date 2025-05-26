package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.entity.Servicio;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class ProveedorTest {

    @Test
    void defaultValues() {
        Proveedor p = new Proveedor();
        // rating default
        assertThat(p.getRating())
                .as("Rating debe inicializarse en cero")
                .isEqualByComparingTo(BigDecimal.ZERO);
        // listas nulas o vacías según implementación
        assertThat(p.getServicios()).isNullOrEmpty();
        assertThat(p.getUser()).isNull();
    }

    @Test
    void gettersAndSettersWork() {
        Proveedor p = new Proveedor();
        p.setId(10L);
        p.setNombre("ProveedorX");
        p.setDescripcion("Descripción larga");
        p.setTelefono("555-1234");
        BigDecimal rating = new BigDecimal("4.5");
        p.setRating(rating);

        List<Servicio> servicios = new ArrayList<>();
        p.setServicios(servicios);

        User u = new User();
        u.setId(99L);
        u.setEmail("u@example.com");
        u.setPassword("pw");
        p.setUser(u);

        assertThat(p.getId()).isEqualTo(10L);
        assertThat(p.getNombre()).isEqualTo("ProveedorX");
        assertThat(p.getDescripcion()).isEqualTo("Descripción larga");
        assertThat(p.getTelefono()).isEqualTo("555-1234");
        assertThat(p.getRating()).isEqualByComparingTo(rating);
        assertThat(p.getServicios()).isSameAs(servicios);
        assertThat(p.getUser()).isSameAs(u);
    }

    @Test
    void equalsAndHashCode_considerAllFields() {
        Proveedor p1 = new Proveedor();
        p1.setId(1L);
        p1.setNombre("A");
        p1.setDescripcion("D");
        p1.setTelefono("T");
        p1.setRating(new BigDecimal("3.0"));

        Proveedor p2 = new Proveedor();
        p2.setId(1L);
        p2.setNombre("A");
        p2.setDescripcion("D");
        p2.setTelefono("T");
        p2.setRating(new BigDecimal("3.0"));

        assertThat(p1).isEqualTo(p2);
        assertThat(p1.hashCode()).isEqualTo(p2.hashCode());
    }

    @Test
    void toString_includesKeyFields() {
        Proveedor p = new Proveedor();
        p.setId(20L);
        p.setNombre("Prov");
        p.setDescripcion("Desc");
        p.setTelefono("123");
        p.setRating(new BigDecimal("2.2"));

        String s = p.toString();
        assertThat(s)
                .contains("id=20")
                .contains("nombre=Prov")
                .contains("descripcion=Desc")
                .contains("telefono=123")
                .contains("rating=2.2");
    }
}

