package com.example.proyecto;

import com.example.proyecto.dto.ClienteRequestDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import com.example.proyecto.BaseIntegrationTest;

public class ClienteControllerIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    public void testCrearCliente() throws Exception {
        ClienteRequestDTO dto = new ClienteRequestDTO();
        dto.setNombre("Juan Perez");
        dto.setEmail("juan@example.com");
        dto.setPassword("secret");
        mockMvc.perform(post("/api/clientes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
    }

    @Test
    public void testMisReservasVacio() throws Exception {
        // Asume cliente con ID 1 existe
        mockMvc.perform(get("/api/clientes/1/reservas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }
}