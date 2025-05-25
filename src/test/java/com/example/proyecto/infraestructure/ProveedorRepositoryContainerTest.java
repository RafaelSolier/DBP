package com.example.proyecto.infraestructure;

import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.infrastructure.ProveedorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
@Testcontainers
@DataJpaTest
public class ProveedorRepositoryContainerTest {
   @Container
   static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
           .withDatabaseName("testdb")
           .withUsername("test")
           .withPassword("test");

   @Autowired
   private ProveedorRepository proveedorRepository;

   @DynamicPropertySource
   static void overrideProps(DynamicPropertyRegistry registry) {
       registry.add("spring.datasource.url",    postgres::getJdbcUrl);
       registry.add("spring.datasource.username", postgres::getUsername);
       registry.add("spring.datasource.password", postgres::getPassword);
   }

   @Test
   void saveAndFindById_shouldReturnProveedor() {
       Proveedor p = new Proveedor();
       User user = new User();
       p.setNombre("Proveedor Test");
       user.setPassword("password");
       user.setEmail("juanito@gmail.com");
       p.setDescripcion("Soy proveedor de limpieza");
       p.setTelefono("987654321");
       proveedorRepository.save(p);

       Optional<Proveedor> found = proveedorRepository.findById(p.getId());
       assertTrue(found.isPresent());
       assertEquals("Proveedor Test", found.get().getNombre());
   }
}
