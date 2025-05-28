package com.example.proyecto.infraestructure;

import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
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
public class UserRepositoryContainerTest {

    @Container
    @ServiceConnection
    public static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("testdb")
            .withUsername("test")
            .withPassword("test");

    @Autowired
    private UserRepository userRepository;

    @BeforeAll
    public void setup() {
        userRepository.deleteAll();
    }

    @Test
    public void saveAndFindByEmail() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("secret");
        user.getRoles().add(Role.ROLE_CLIENTE);
        User saved = userRepository.save(user);

        Optional<User> found = userRepository.findByEmail("user@example.com");
        assertThat(found)
                .as("Debe encontrar el usuario a partir de su email")
                .isPresent();
        User u = found.get();
        assertThat(u.getId()).isEqualTo(saved.getId());
        assertThat(u.getEmail()).isEqualTo("user@example.com");
    }

    @Test
    public void existsByEmail_trueForExisting() {
        User user = new User();
        user.setEmail("exists@example.com");
        user.setPassword("pwd");
        user.getRoles().add(Role.ROLE_CLIENTE);
        userRepository.save(user);

        boolean exists = userRepository.existsByEmail("exists@example.com");
        assertThat(exists)
                .as("existsByEmail debe devolver true para un email existente")
                .isTrue();
    }

    @Test
    public void existsByEmail_falseForNonExisting() {
        boolean exists = userRepository.existsByEmail("nope@example.com");
        assertThat(exists)
                .as("existsByEmail debe devolver false para un email no existente")
                .isFalse();
    }
}
