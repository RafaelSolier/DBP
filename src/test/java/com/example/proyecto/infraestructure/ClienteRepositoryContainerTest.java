// src/test/java/com/example/proyecto/infrastructure/ClienteRepositoryContainerTest.java
package com.example.proyecto.infraestructure;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.infrastructure.ClienteRepository;
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
public class ClienteRepositoryContainerTest {
    @ServiceConnection
    @Container
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setup() {
        clienteRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    public void saveAndFindByUserEmail() {
        // Creación y guardado de User
        User u = new User();
        u.setEmail("test@example.com");
        u.setPassword("password123");
        u.getRoles().add(Role.ROLE_CLIENTE);
        User savedUser = userRepository.save(u);

        // Creación y guardado de Cliente
        Cliente cliente = new Cliente();
        cliente.setUser(savedUser);
        cliente.setNombre("Test Cliente");
        cliente.setTelefono("123456789");
        cliente.setApellido("Test Cliente");
        Cliente savedClient = clienteRepository.save(cliente);

        // Búsqueda por email de User
        Optional<Cliente> found = clienteRepository.findByEmail("test@example.com");
        assertThat(found)
                .as("Debe encontrar el cliente a partir del email del User")
                .isPresent();
        Cliente f = found.get();
        assertThat(f.getId()).isEqualTo(savedClient.getId());
        assertThat(f.getUser().getEmail()).isEqualTo("test@example.com");
    }

    @Test
    public void findByNonExistentUserEmail_returnsEmpty() {
        Optional<Cliente> opt = clienteRepository.findByEmail("nope@example.com");
        assertThat(opt)
                .as("No debe encontrar nada para un email no existente")
                .isEmpty();
    }
}
