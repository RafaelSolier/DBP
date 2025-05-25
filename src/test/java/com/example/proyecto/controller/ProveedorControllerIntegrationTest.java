package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.*;
import com.example.proyecto.dto.ProveedorRequestDto;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.dto.DisponibilidadDTO;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.domain.enums.DiaSemana;
import com.example.proyecto.domain.enums.EstadoReserva;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import com.example.proyecto.infrastructure.DisponibilidadRepository;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc(addFilters = false)
@SpringBootTest
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
class ProveedorControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private ProveedorRepository proveedorRepo;
    @Autowired private ServicioRepository servicioRepo;
    @Autowired private DisponibilidadRepository disponibilidadRepo;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ReservaRepository reservaRepo;

    private ProveedorRequestDto validProveedor;
    private ServicioRequestDto validServicio;
    private DisponibilidadDTO validHorario;

    @BeforeEach
    void setup() {
        reservaRepo.deleteAll();
        disponibilidadRepo.deleteAll();
        servicioRepo.deleteAll();
        proveedorRepo.deleteAll();
        clienteRepo.deleteAll();

        validProveedor = new ProveedorRequestDto();
        validProveedor.setNombre("ProveedorX");
        validProveedor.setEmail("prov@example.com");
        validProveedor.setPassword("pass123");
        validProveedor.setDescripcion("Descripción del proveedor");
        validProveedor.setTelefono("987654321");

        validServicio = new ServicioRequestDto();
        validServicio.setNombre("Limpieza");
        validServicio.setDescripcion("Limpieza de oficinas");
        validServicio.setPrecio(new BigDecimal("150.00"));
        validServicio.setCategoria("LIMPIEZA");

        validHorario = new DisponibilidadDTO();
        validHorario.setDiaSemana(DiaSemana.LUNES);
        validHorario.setHoraInicio(LocalTime.of(9, 0));
        validHorario.setHoraFin(LocalTime.of(17, 0));
    }

    @Test
    void crearProveedor_withValidData_returns201() throws Exception {
        mockMvc.perform(post("/api/proveedores")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validProveedor)))
                .andExpect(status().isCreated());

        assertThat(proveedorRepo.findAll()).hasSize(1);
    }

    @Test
    void agregarServicio_toExistingProveedor_returns201AndSaves() throws Exception {
        // Crear proveedor primero
        mockMvc.perform(post("/api/proveedores")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validProveedor)))
                .andExpect(status().isCreated());

        Long provId = proveedorRepo.findAll().get(0).getId();
        mockMvc.perform(post("/api/proveedores/{id}/servicios", provId)
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(validServicio)))
                .andExpect(status().isCreated());

        assertThat(servicioRepo.findAll()).hasSize(1);
    }

    @Test
    void establecerHorarios_existingService_returns204AndSaves() throws Exception {
        // Setup: crear Proveedor y Servicio directamente
        User user = new User();
        user.setEmail(validProveedor.getEmail());
        user.setPassword(validProveedor.getPassword());
        Proveedor prov = new Proveedor();
        prov.setNombre(validProveedor.getNombre());
//        prov.setEmail(validProveedor.getEmail());
//        prov.setPassword(validProveedor.getPassword());
        prov.setDescripcion(validProveedor.getDescripcion());
        prov.setTelefono(validProveedor.getTelefono());
        prov = proveedorRepo.save(prov);

        Servicio svc = new Servicio();
        svc.setNombre(validServicio.getNombre());
        svc.setDescripcion(validServicio.getDescripcion());
        svc.setPrecio(validServicio.getPrecio());
        svc.setCategoria(Categorias.valueOf(validServicio.getCategoria()));
        svc.setProveedor(prov);
        svc = servicioRepo.save(svc);

        mockMvc.perform(post("/api/servicios/{id}/horarios", svc.getId())
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(List.of(validHorario))))
                .andExpect(status().isNoContent());

        assertThat(disponibilidadRepo.findByServicio(svc)).hasSize(1);
    }

    @Test
    void obtenerReservas_existingReservations_returnsList() throws Exception {
        User user = new User();
        user.setEmail(validProveedor.getEmail());
        user.setPassword(validProveedor.getPassword());
        // Setup: crear datos de dominio
        Proveedor prov = new Proveedor();
        prov.setNombre(validProveedor.getNombre());
//        prov.setEmail(validProveedor.getEmail());
//        prov.setPassword(validProveedor.getPassword());
        prov.setDescripcion(validProveedor.getDescripcion());
        prov.setTelefono(validProveedor.getTelefono());
        prov = proveedorRepo.save(prov);

        Servicio svc = new Servicio();
        svc.setNombre(validServicio.getNombre());
        svc.setDescripcion(validServicio.getDescripcion());
        svc.setPrecio(validServicio.getPrecio());
        svc.setCategoria(Categorias.valueOf(validServicio.getCategoria()));
        svc.setProveedor(prov);
        svc = servicioRepo.save(svc);

        Cliente cli = new Cliente();
        cli.setNombre("ClienteA");
        cli.setApellido("Test");
        user.setEmail("cliente@test.com");
        cli.setTelefono("900000001");
        user.setPassword("pwd");
        cli = clienteRepo.save(cli);

        Reserva r = new Reserva();
        r.setFechaReserva(LocalDateTime.now());
        r.setDireccion("Av Test 123");
        r.setEstado(EstadoReserva.PENDIENTE);
        r.setCliente(cli);
        r.setServicio(svc);
        reservaRepo.save(r);

        mockMvc.perform(get("/api/proveedores/{id}/reservas", prov.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clienteId").value(cli.getId()))
                .andExpect(jsonPath("$[0].servicioId").value(svc.getId()));
    }
}
