package com.example.proyecto.controller;

import com.example.proyecto.infrastructure.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ReservaControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ClienteRepository clienteRepo;
    @Autowired ProveedorRepository proveedorRepo;
    @Autowired ServicioRepository servicioRepo;
    @Autowired ReservaRepository reservaRepo;

    private String jwtCliente;
    private Long clienteId;
    private String jwtProveedor;
    private Long proveedorId;
    private Long servicioId;
    private Long reservaId;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Registro y login CLIENTE
        var regCli = Map.of(
                "nombre",   "Juan",
                "apellido", "Pérez",
                "email",    "juan@mail.com",
                "telefono", "123",
                "password", "Pass1!"
        );
        String cliJson = mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regCli)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode cliNode = mapper.readTree(cliJson);
        jwtCliente = cliNode.get("token").asText();
        clienteId  = clienteRepo.findAll().get(0).getId();

        // 2) Registro y login PROVEEDOR
        var regProv = Map.of(
                "nombre",   "Ana",
                "apellido", "Gómez",
                "email",    "ana@mail.com",
                "telefono", "456",
                "password", "Prov1!"
        );
        String provJson = mvc.perform(post("/auth/register/proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regProv)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode provNode = mapper.readTree(provJson);
        jwtProveedor = provNode.get("token").asText();
        proveedorId  = proveedorRepo.findAll().get(0).getId();

        // 3) Crear un servicio con PROVEEDOR
        var serv = Map.of(
                "nombre",      "Limpieza",
                "descripcion", "Casa entera",
                "precio",      30.0,
                "categoria",   "LIMPIEZA"
        );
        mvc.perform(post("/api/proveedores/{id}/servicios", proveedorId)
                        .header("Authorization","Bearer "+jwtProveedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(serv)))
                .andExpect(status().isCreated());
        servicioId = servicioRepo.findAll().stream()
                .filter(s -> s.getProveedor().getId().equals(proveedorId))
                .findFirst().orElseThrow().getId();

        // 4) Crear una reserva con CLIENTE
        var res = Map.of(
                "servicioId", servicioId,
                "fechaReserva","2025-06-20T10:00:00",
                "direccion","Calle Falsa 123"
        );
        String resJson = mvc.perform(post("/api/clientes/{id}/reservas", clienteId)
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(res)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        reservaId = mapper.readTree(resJson).get("id").asLong();
    }

    @Test
    void listarTodas_conCliente_devuelve200YLista() throws Exception {
        mvc.perform(get("/api/reservas")
                        .header("Authorization","Bearer "+jwtCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(reservaId));
    }

    @Test
    void listarTodas_conProveedor_devuelve200YLista() throws Exception {
        mvc.perform(get("/api/reservas")
                        .header("Authorization","Bearer "+jwtProveedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id").value(reservaId));
    }

    @Test
    void listarTodas_sinToken_devuelve401() throws Exception {
        mvc.perform(get("/api/reservas"))
                .andExpect(status().isUnauthorized());
    }
}