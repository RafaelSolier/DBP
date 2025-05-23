package com.example.proyecto.controller;

import com.example.proyecto.dto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;

    private ClienteRequestDTO validCliente;
    private ProveedorRequestDto validProveedor;
    private ServicioRequestDto validServicio;

    @BeforeEach
    void setup() {
        validCliente = new ClienteRequestDTO();
        validCliente.setNombre("Juan");
        validCliente.setApellido("Perez");
        validCliente.setEmail("juan.perez@example.com");
        validCliente.setTelefono("987654321");
        validCliente.setPassword("secret123");

        validProveedor = new ProveedorRequestDto();
        validProveedor.setNombre("Proveedor");
        validProveedor.setEmail("xd.perez@example.com");
        validProveedor.setTelefono("987654321");
        validProveedor.setDescripcion("Limpieza");

        validServicio = new ServicioRequestDto();
        validServicio.setNombre("Limpieza");
        validServicio.setDescripcion("Limpieza de oficina");
        validServicio.setPrecio(new BigDecimal(100));
        validServicio.setCategoria("PLOMERIA");


    }

    @Test
    void register_withValidData_returns201() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value("juan.perez@example.com"));
    }

    @Test
    void register_withMissingFields_returns400() throws Exception {
        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation Failed"))
                .andExpect(jsonPath("$.messages").isArray());
    }

    @Test
    void register_duplicateEmail_returns409() throws Exception {
        // primer registro
        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente)))
                .andExpect(status().isCreated());
        // segundo registro mismo email
        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Conflict"));
    }

    @Test
    void login_withValidCreds_returns200WithToken() throws Exception {
        // registrar primero
        mockMvc.perform(post("/api/clientes")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente)))
                .andExpect(status().isCreated());
        // login
        LoginDTO login = new LoginDTO();
        login.setEmail("juan.perez@example.com");
        login.setPassword("secret123");
        mockMvc.perform(post("/api/clientes/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void login_withInvalidCreds_returns401() throws Exception {
        LoginDTO login = new LoginDTO();
        login.setEmail("noexist@example.com");
        login.setPassword("wrong");
        mockMvc.perform(post("/api/clientes/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Unauthorized"));
    }

    @Test
    void getServicios_noData_returnsEmptyList() throws Exception {
        mockMvc.perform(get("/api/servicios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }

    @Test
    void createReserva_nonexistentService_returns404() throws Exception {
        // cliente debe existir
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validCliente)))
                .andExpect(status().isCreated());
        // intento reserva en servicio id=999 (incluye direccion requerida)
        mockMvc.perform(post("/api/clientes/1/reservas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"servicioId\":999,\"clienteId\":1,\"fechaReserva\":\"2025-06-01T10:00:00\",\"direccion\":\"Av. Siempre Viva 742\"}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void cancelReserva_nonexistent_returns404() throws Exception {
        mockMvc.perform(patch("/api/clientes/1/reservas/1/cancelar"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }

    @Test
    void listReservas_nonexistentCliente_returns404() throws Exception {
        mockMvc.perform(get("/api/clientes/1/reservas"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}