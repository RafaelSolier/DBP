package com.example.proyecto.controller;

import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ResenaRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ResenaControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ClienteRepository clienteRepo;
    @Autowired ProveedorRepository proveedorRepo;
    @Autowired ServicioRepository servicioRepo;
    @Autowired ResenaRepository resenaRepo;

    private String jwtCliente;
    private String jwtProveedor;
    private Long clienteId;
    private Long proveedorId;
    private Long servicioId;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Registrar y autenticar cliente
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

        // 2) Registrar proveedor y crear servicio
        var regProv = Map.of(
                "nombre",    "Ana",
                "apellido",  "Gómez",
                "email",     "ana@mail.com",
                "telefono",  "456",
                "password",  "Prov1!"
        );
        String provJson = mvc.perform(post("/auth/register/proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regProv)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode p = mapper.readTree(provJson);
        jwtProveedor = p.get("token").asText();
        //clienteId  = clienteRepo.findAll().get(0).getId();
        proveedorId = proveedorRepo.findAll().get(0).getId();

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
                .filter(s->s.getProveedor().getId().equals(proveedorId))
                .findFirst().orElseThrow().getId();
    }

    @Test
    void crearResena_conDatosValidos_devuelve201() throws Exception {
        var req = Map.of(
                "servicioId", servicioId,
                "clienteId",  clienteId,
                "fecha",       LocalDateTime.now().toString(),
                "calificacion", 5,
                "comentario",   "Excelente servicio"
        );
        String json = mapper.writeValueAsString(req);

        String resp = mvc.perform(post("/api/resenas")
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        Long id = mapper.readTree(resp).get("id").asLong();
        assertThat(resenaRepo.findById(id)).isPresent();
    }

    @Test
    void crearResena_sinToken_devuelve401() throws Exception {
        var req = Map.of(
                "servicioId", servicioId,
                "calificacion", 4,
                "comentario",   "Bueno"
        );
        mvc.perform(post("/api/resenas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void crearResena_datosInvalidos_devuelve400() throws Exception {
        // Por ejemplo falta servicioId o calificación fuera de rango
        var req = Map.of(
                "servicioId", servicioId,
                "calificacion", 10,      // asumiendo rango 1–5
                "comentario",   ""
        );
        mvc.perform(post("/api/resenas")
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(req)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }

    @Test
    void obtenerResenasPorServicio_devuelve200YLista() throws Exception {
        // primero creamos dos reseñas
        for (int i = 1; i <= 2; i++) {
            var req = Map.of(
                    "servicioId", servicioId,
                        "clienteId", clienteId,
                    "calificacion", i,
                    "fecha", LocalDateTime.now().toString(),
                    "comentario",   "Texto " + i
            );
            mvc.perform(post("/api/resenas")
                            .header("Authorization","Bearer "+jwtCliente)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(mapper.writeValueAsString(req)))
                    .andExpect(status().isCreated());
        }

        mvc.perform(get("/api/servicios/{servicioId}/resenas", servicioId)
                        .header("Authorization","Bearer "+jwtCliente))
                .andExpect(status().isOk());

    }
}