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
import java.math.BigDecimal;
import java.util.Map;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class PagoControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired
    ClienteRepository clienteRepo;
    @Autowired
    ProveedorRepository proveedorRepo;
    @Autowired
    ServicioRepository servicioRepo;
    @Autowired
    ReservaRepository reservaRepo;
    @Autowired
    PagoRepository pagoRepo;

    private String jwtCliente;
    private Long clienteId;
    private Long proveedorId;
    private Long servicioId;
    private Long reservaId;
    private String jwtProveedor;

    @BeforeEach
    void setUp() throws Exception {
        // 1) Registro y login de cliente
        var regCli = Map.of(
                "nombre","Juan","apellido","Pérez",
                "email","juan@mail.com","telefono","123","password","Pass1!"
        );
        String cliJson = mvc.perform(post("/auth/register/cliente")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regCli)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode cliNode = mapper.readTree(cliJson);
        jwtCliente = cliNode.get("token").asText();
        clienteId  = clienteRepo.findAll().get(0).getId();

        // 2) Registro de proveedor y creación de servicio
        var regProv = Map.of(
                "nombre","Ana", "descripcion","Gómez",
                "email","ana@mail.com","telefono","456","password","Prov1!"
        );
        String provJson = mvc.perform(post("/auth/register/proveedor")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regProv)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        JsonNode p = mapper.readTree(provJson);
        // no necesitamos token de proveedor aquí, solo sacamos el repositorio
        jwtProveedor = p.get("token").asText();     // guarda el token
        proveedorId  = proveedorRepo.findAll().get(0).getId();

        var serv = Map.of(
                "nombre","Limpieza","descripcion","Casa entera",
                "precio",30.0,"categoria","LIMPIEZA"
        );
        mvc.perform(post("/api/proveedores/{id}/servicios", proveedorId)
                        .header("Authorization","Bearer "+jwtProveedor)// o jwtProveedor si lo guardas
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(serv)))
                .andExpect(status().isCreated());
        servicioId = servicioRepo.findAll().stream()
                .filter(s->s.getProveedor().getId().equals(proveedorId))
                .findFirst().orElseThrow().getId();

        // 3) Crear reserva para luego pagarla
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
    void procesarPago_conDatosValidos_devuelve201() throws Exception {
        // Ajusta los campos a tu DTO real
        var pagoDto = Map.of(
                "monto", new BigDecimal("30.00"),
                "estado", "PENDIENTE"
        );
        String pagoJson = mapper.writeValueAsString(pagoDto);

        String resp = mvc.perform(post("/api/pagos/{resId}", reservaId)
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(pagoJson))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        // Verificar que se guardó en BD
        Long pagoId = mapper.readTree(resp).get("id").asLong();
        assertThat(pagoRepo.findById(pagoId)).isPresent();
    }

    @Test
    void procesarPago_sinToken_devuelve401() throws Exception {
        var pagoDto = Map.of("monto",10.0,"estado", "PENDIENTE");
        mvc.perform(post("/api/pagos/{resId}", reservaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pagoDto)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void procesarPago_reservaNoExiste_devuelve404() throws Exception {
        var pagoDto = Map.of("monto",10.0,"estado", "PENDIENTE");
        mvc.perform(post("/api/pagos/{resId}", 99999L)
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pagoDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void procesarPago_datosInvalidos_devuelve400() throws Exception {
        // Por ejemplo, monto negativo
        var pagoDto = Map.of("monto",-5.0,"estado","PENDIENTE");
// Omitir campos o poner formato incorrecto según tu DTO
        mvc.perform(post("/api/pagos/{resId}", reservaId)
                        .header("Authorization","Bearer "+jwtCliente)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(pagoDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors").exists());
    }
}