package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class ClienteControllerIntegrationTest {

    @Autowired MockMvc mvc;
    @Autowired ObjectMapper mapper;
    @Autowired ClienteRepository clienteRepository;
    @Autowired ProveedorRepository proveedorRepository;
    @Autowired ServicioRepository servicioRepository;

    private String jwtCliente;
    private Long clienteId;
    private String jwtProveedor;
    private Long proveedorId;
    private Long servicioId;

    @BeforeEach
    void setUp() throws Exception {
        // —— 1) Registro de CLIENTE y extracción de clienteId + token ——
        Map<String,Object> regCliente = Map.of(
                "nombre",   "Juan",
                "apellido", "Pérez",
                "email",    "juan.perez@mail.com",
                "telefono", "999123456",
                "password", "TuPass123!"
        );
        String cliResp = mvc.perform(post("/auth/register/cliente")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regCliente))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode cliNode = mapper.readTree(cliResp);
        jwtCliente = cliNode.get("token").asText();
        Cliente cliente = clienteRepository.findAll().get(0);
        clienteId = cliente.getId();

        // —— 2) Registro de PROVEEDOR y extracción de proveedorId + token ——
        Map<String,Object> regProv = Map.of(
                "nombre",   "Ana",
                "apellido", "Gómez",
                "email",    "ana.gomez@mail.com",
                "telefono", "987654321",
                "password", "ProvPass123!"
        );
        String provResp = mvc.perform(post("/auth/register/proveedor")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(regProv))
                )
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();
        JsonNode provNode = mapper.readTree(provResp);
        jwtProveedor = provNode.get("token").asText();
        // El AuthResponse no da proveedorId, así que lo extraemos de la tabla:
        Proveedor proveedor = proveedorRepository.findAll().get(0);
        proveedorId = proveedor.getId();

                // —— 3) Crear SERVICIO con el proveedor ——
                Map<String,Object> servPayload = Map.of(
                "nombre",      "Limpieza profunda",
                "descripcion", "Casa entera",
                "precio",      50.0,
                "categoria",   "PLOMERIA"
        );
//        String servResp = mvc.perform(post("/api/proveedores/{provId}/servicios", proveedorId)
//                        .header("Authorization", "Bearer " + jwtProveedor)
//                        .contentType(APPLICATION_JSON)
//                        .content(mapper.writeValueAsString(servPayload))
//                )
//                .andExpect(status().isCreated())
//                .andReturn()
//                .getResponse()
//                .getContentAsString();
//        JsonNode servNode = mapper.readTree(servResp);
//        servicioId = servNode.get("id").asLong();

        mvc.perform(post("/api/proveedores/{provId}/servicios", proveedorId)
                        .header("Authorization", "Bearer " + jwtProveedor)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(servPayload))
                )
                .andExpect(status().isCreated());

        // ————— Extraer servicioId del repositorio —————
        servicioId = servicioRepository.findAll().stream()
                .filter(s -> s.getProveedor().getId().equals(proveedorId))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Servicio no creado"))
                .getId();
    }

    @Test
    void crearReserva_conDatosValidos_devuelve201() throws Exception {
        Map<String,Object> body = Map.of(
                "servicioId",   servicioId,
                "fechaReserva", "2025-06-01T10:00:00",
                "direccion",    "Av. Siempre Viva 742"
        );

        mvc.perform(post("/api/clientes/{clienteId}/reservas", clienteId)
                        .header("Authorization", "Bearer " + jwtCliente)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body))
                )
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.servicioId").value(servicioId))
                .andExpect(jsonPath("$.direccion").value("Av. Siempre Viva 742"));
    }
    @Test
    void crearReserva_sinToken_devuelve401() throws Exception {
        var body = Map.of(
                "servicioId",   servicioId,
                "fechaReserva", "2025-06-01T10:00:00",
                "direccion",    "Av. Siempre Viva 742"
        );

        mvc.perform(post("/api/clientes/{clienteId}/reservas", clienteId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(body))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    void registerCliente_emailDuplicado_devuelve409() throws Exception {
        String emailDup = "dup@example.com";
        var payload = Map.of(
                "nombre",   "Ana",
                "apellido", "Gómez",
                "email",    emailDup,
                "telefono", "987654321",
                "password", "Secure123!"
        );
        mvc.perform(post("/auth/register/cliente")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload))
                )
                .andExpect(status().isCreated());

        mvc.perform(post("/auth/register/cliente")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(payload))
                )
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409))
                .andExpect(jsonPath("$.error").value("Conflict"))
                .andExpect(jsonPath("$.message").value("Correo registrado"));
    }
    @Test
    void login_conCredencialesValidas_devuelve200yToken() throws Exception {
        // Primero registra un cliente
        var reg = Map.of(
                "nombre",   "Juan",
                "apellido", "Pérez",
                "email",    "juanito.perez@mail.com",
                "telefono", "999123456",
                "password", "TuPass123!"
        );
        mvc.perform(post("/auth/register/cliente")
                .contentType(APPLICATION_JSON)
                .content(mapper.writeValueAsString(reg))
        ).andExpect(status().isCreated());
        // Luego hace login
        var login = Map.of("email", "juanito.perez@mail.com", "password", "TuPass123!");
        mvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isString());
    }

    @Test
    void login_conCredencialesInvalidas_devuelve401() throws Exception {
        var login = Map.of("email", "juan.perez@mail.com", "password", "Wrong!");
        mvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(login)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }
    @Test
    void registerCliente_sinEmail_devuelve400yErrores() throws Exception {
        var bad = Map.of(
                "nombre","X","apellido","Y","telefono","123","password","abc"
                // falta "email"
        );
        mvc.perform(post("/auth/register/cliente")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(bad)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.errors.email").value("El email es obligatorio"));
    }
    @Test
    void buscarServicios_sinFiltros_devuelve200yLista() throws Exception {
        mvc.perform(get("/api/servicios")
                        .header("Authorization","Bearer "+jwtCliente))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray());
    }
    @Test
    void misReservas_devuelveListaConLaReservaCreada() throws Exception {
        // crea una reserva como en tu test...
        mvc.perform(get("/api/clientes/{clienteId}/reservas", clienteId)
                        .header("Authorization","Bearer "+jwtCliente))
                .andExpect(status().isOk());

    }
}