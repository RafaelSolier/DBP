package com.example.proyecto.controller;

import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ClienteRepository clienteRepo;
    @Autowired ProveedorRepository proveedorRepo;
    @Autowired UserRepository userRepo;

    private Map<String,Object> validCliente = Map.of(
            "nombre",   "Juan",
            "apellido", "Pérez",
            "email",    "juan@mail.com",
            "telefono", "999123456",
            "password", "Pass1!"
    );

    private Map<String,Object> validProveedor = Map.of(
            "nombre",   "Ana",
            "apellido", "Gómez",
            "descripcion","Servicio X",
            "email",    "ana@mail.com",
            "telefono", "987654321",
            "password", "Prov1!"
    );

    @Test
    void registerCliente_conDatosValidos_devuelve201yToken() throws Exception {
        String resp = mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andReturn().getResponse().getContentAsString();

        // El cliente fue guardado
        assertThat(userRepo.findByEmail("juan@mail.com")).isPresent();
    }

    @Test
    void registerCliente_emailDuplicado_devuelve409() throws Exception {
        // primer registro
        mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente))
                )
                .andExpect(status().isCreated());

        // segundo registro con mismo email
        mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void registerCliente_datosInvalidos_devuelve400() throws Exception {
        // falta email y password demasiado corto
        var bad = Map.of(
                "nombre", "X",
                "apellido", "Y",
                "telefono", "123"
        );
        mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").exists())
                .andExpect(jsonPath("$.errors.password").exists());
    }

    @Test
    void registerProveedor_conDatosValidos_devuelve201yToken() throws Exception {
        String resp = mvc.perform(post("/auth/register/proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validProveedor))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isString())
                .andReturn().getResponse().getContentAsString();

        assertThat(userRepo.findByEmail("ana@mail.com")).isPresent();
    }

    @Test
    void login_conCredencialesValidas_devuelve200yToken() throws Exception {
        // registra cliente primero
        mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente))
                )
                .andExpect(status().isCreated());

        var login = Map.of(
                "email",    "juan@mail.com",
                "password", "Pass1!"
        );
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void login_conCredencialesInvalidas_devuelve401() throws Exception {
        // registra cliente primero
        mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente))
                )
                .andExpect(status().isCreated());

        var login = Map.of(
                "email",    "juan@mail.com",
                "password", "WrongPass"
        );
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login))
                )
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void login_datosInvalidos_devuelve400() throws Exception {
        // falta password
        var bad = Map.of("email", "juan@mail.com");
        mvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad))
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").doesNotExist()) // email está
                .andExpect(jsonPath("$.errors.password").exists());
    }
}