package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.Role;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class UserTest {

    @Test
    void defaultRolesIsEmpty() {
        User u = new User();
        assertThat(u.getRoles())
                .as("Roles should be empty by default")
                .isEmpty();
    }

    @Test
    void addAndRemoveRoles() {
        User u = new User();
        u.getRoles().add(Role.ROLE_CLIENTE);
        assertThat(u.getRoles())
                .containsExactly(Role.ROLE_CLIENTE);

        u.getRoles().remove(Role.ROLE_CLIENTE);
        assertThat(u.getRoles())
                .isEmpty();
    }

    @Test
    void gettersAndSettersWork() {
        User u = new User();
        u.setId(42L);
        u.setEmail("foo@bar.com");
        u.setPassword("secret");
        u.getRoles().add(Role.ROLE_PROVEEDOR);

        assertThat(u.getId()).isEqualTo(42L);
        assertThat(u.getEmail()).isEqualTo("foo@bar.com");
        assertThat(u.getPassword()).isEqualTo("secret");
        assertThat(u.getRoles())
                .containsExactly(Role.ROLE_PROVEEDOR);
    }

    @Test
    void equalsAndHashCode_considerAllFields() {
        User u1 = new User();
        u1.setId(1L);
        u1.setEmail("a@b.com");
        u1.setPassword("pwd");
        u1.getRoles().add(Role.ROLE_CLIENTE);

        User u2 = new User();
        u2.setId(1L);
        u2.setEmail("a@b.com");
        u2.setPassword("pwd");
        u2.getRoles().add(Role.ROLE_CLIENTE);

        assertThat(u1)
                .as("Users with same id, email, password and roles should be equal")
                .isEqualTo(u2);
        assertThat(u1.hashCode())
                .isEqualTo(u2.hashCode());
    }

    @Test
    void toString_includesKeyFields() {
        User u = new User();
        u.setId(99L);
        u.setEmail("x@y.com");
        u.getRoles().add(Role.ROLE_PROVEEDOR);

        String str = u.toString();
        assertThat(str)
                .contains("id=99")
                .contains("email=x@y.com")
                .contains("ROLE_PROVEEDOR");
    }
}
