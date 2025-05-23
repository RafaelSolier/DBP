// src/test/java/com/example/proyecto/infrastructure/ClienteRepositoryContainerTest.java
package com.example.proyecto.infraestructure;
import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.infrastructure.ClienteRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
@DataJpaTest
class ClienteRepositoryContainerTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private ClienteRepository clienteRepository;

    @DynamicPropertySource
    static void overrideProps(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url",    postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void saveAndFindByEmail_shouldReturnCliente() {
        Cliente c = new Cliente();
        c.setNombre("Test");
        c.setApellido("User");
        c.setEmail("test@ct.com");
        c.setTelefono("123456");
        c.setPassword("pwd");
        clienteRepository.save(c);

        Optional<Cliente> found = clienteRepository.findByEmail("test@ct.com");
        assertTrue(found.isPresent());
        assertEquals("Test", found.get().getNombre());
    }
}
