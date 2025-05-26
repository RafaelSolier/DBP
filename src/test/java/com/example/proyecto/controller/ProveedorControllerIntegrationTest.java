package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.*;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ProveedorControllerIntegrationTest {

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
        // --- 1) Registrar CLIENTE ---
        var regCliente = Map.of(
                "nombre","Juan","apellido","Pérez",
                "email","juan@mail.com","telefono","123","password","Pass1!"
        );
        String cJs = mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regCliente)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode cN = mapper.readTree(cJs);
        jwtCliente = cN.get("token").asText();
        clienteId  = clienteRepo.findAll().get(0).getId();

        // --- 2) Registrar PROVEEDOR ---
        var regProv = Map.of(
                "nombre","Ana","apellido","Gómez",
                "email","ana@mail.com","telefono","456","password","Prov1!"
        );
        String pJs = mvc.perform(post("/auth/register/proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regProv)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode pN = mapper.readTree(pJs);
        jwtProveedor = pN.get("token").asText();
        proveedorId  = proveedorRepo.findAll().get(0).getId();

        // --- 3) Crear SERVICIO ---
        var serv = Map.of(
                "nombre","Limpieza","descripcion","Casa entera",
                "precio", 30.0,"categoria","LIMPIEZA"
        );
        mvc.perform(post("/api/proveedores/{id}/servicios", proveedorId)
                        .header("Authorization","Bearer "+jwtProveedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(serv)))
                .andExpect(status().isCreated());
        Servicio s = servicioRepo.findAll().stream()
                .filter(x-> x.getProveedor().getId().equals(proveedorId))
                .findFirst().orElseThrow();
        servicioId = s.getId();

        // --- 4) Crear una RESERVA como CLIENTE ---
        var res = Map.of(
                "servicioId", servicioId,
                "fechaReserva","2025-06-15T10:00:00",
                "direccion","Av. Siempre Viva 742"
        );
        mvc.perform(post("/api/clientes/{id}/reservas", clienteId)
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(res)))
                .andExpect(status().isCreated());
        Reserva r = reservaRepo.findAll().get(0);
        reservaId = r.getId();
    }

    @Test
    void agregarServicio_conTokenProveedor_devuelve201() throws Exception {
        var dto = Map.of(
                "nombre","Ventanas","descripcion","Vidrios","precio",50.0,"categoria","LIMPIEZA"
        );
        mvc.perform(post("/api/proveedores/{id}/servicios", proveedorId)
                        .header("Authorization","Bearer "+jwtProveedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated());
        // Debe existir un segundo servicio
        assertThat(servicioRepo.count()).isEqualTo(2);
    }

    @Test
    void agregarServicio_sinToken_devuelve401() throws Exception {
        mvc.perform(post("/api/proveedores/{id}/servicios", proveedorId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isUnauthorized());
    }

@Test
void agregarServicio_conTokenCliente_devuelve403() throws Exception {
    var dto = Map.of(
            "nombre", "X",
            "descripcion", "Y",
            "precio", 1.0,
            "categoria", "LIMPIEZA"
    );

    Assertions.assertThrows(
            AuthorizationDeniedException.class,
            () -> mvc.perform(post("/api/proveedores/{id}/servicios", proveedorId)
                    .header("Authorization","Bearer "+jwtCliente)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(dto))
            ).andReturn()
    );
}

    @Test
    void actualizarServicio_valido_devuelve200() throws Exception {
        // Usamos aquí la misma categoría original, que sabemos que existe en el enum
        var update = Map.of(
                "nombre",      "LimpiezaX",
                "descripcion", "Detalle",
                "precio",      99.0,
                "categoria",   "LIMPIEZA"    // <- antes tenías "OTRO", que no existe
        );

        mvc.perform(put("/api/servicios/{id}", servicioId)
                        .header("Authorization","Bearer "+jwtProveedor)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(update)))
                .andExpect(status().isOk());

        // Verificamos además que sí se actualizó el nombre
        var srv = servicioRepo.findById(servicioId).orElseThrow();
        assertThat(srv.getNombre()).isEqualTo("LimpiezaX");
        // Y (opcional) que la categoría sigue siendo una de las válidas:
        assertThat(srv.getCategoria()).isEqualTo(Categorias.LIMPIEZA);
    }

    @Test
    void obtenerReservas_conTokenProveedor_devuelve200YLista() throws Exception {
        mvc.perform(get("/api/proveedores/{id}/reservas", proveedorId)
                        .header("Authorization","Bearer "+jwtProveedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(reservaId));
    }

    @Test
    void aceptarYCompletarReserva_devuelven200() throws Exception {
        // Aceptar
        mvc.perform(patch("/api/reservas/{resId}/aceptar", reservaId)
                        .header("Authorization","Bearer "+jwtProveedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("ACEPTADA"));

        // Completar
        mvc.perform(patch("/api/reservas/{resId}/completar", reservaId)
                        .header("Authorization","Bearer "+jwtProveedor))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.estado").value("COMPLETADA"));
    }
}
