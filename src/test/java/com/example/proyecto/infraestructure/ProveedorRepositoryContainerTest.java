package com.example.proyecto.infraestructure;

import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.UserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import java.util.Optional;
import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ProveedorRepositoryContainerTest {
    @ServiceConnection
    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ProveedorRepository proveedorRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setup() {
        proveedorRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void saveAndFindByUserEmail() {
        // Creación y guardado de User
        User u = new User();
        u.setEmail("tes@example.com");
        u.setPassword("password123");
        u.getRoles().add(Role.ROLE_PROVEEDOR);
        User savedUser = userRepository.save(u);

        // Creación y guardado de Cliente
        Proveedor proveedor = new Proveedor();
        proveedor.setUser(savedUser);
        proveedor.setNombre("Test Cliente");
        proveedor.setTelefono("123456789");
        proveedor.setDescripcion("Test Cliente");
        Proveedor savedClient = proveedorRepository.save(proveedor);

        // Búsqueda por email de User
        Optional<Proveedor> found = proveedorRepository.findByEmail("tes@example.com");
        assertThat(found)
                .as("Debe encontrar el proveedor a partir del email del User")
                .isPresent();
        Proveedor f = found.get();
        assertThat(f.getId()).isEqualTo(savedClient.getId());
        assertThat(f.getUser().getEmail()).isEqualTo("tes@example.com");
    }

    @Test
    public void findByNonExistentUserEmail_returnsEmpty() {
        Optional<Proveedor> opt = proveedorRepository.findByEmail("nope@example.com");
        assertThat(opt)
                .as("No debe encontrar nada para un email no existente")
                .isEmpty();
    }
}
