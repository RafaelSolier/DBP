package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.*;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.domain.enums.EstadoReserva;
import com.example.proyecto.dto.ProveedorRequestDto;
import com.example.proyecto.dto.ReservaDTO;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ReservaRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
public class ReservaControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ServicioRepository servicioRepo;
    @Autowired private ProveedorRepository proveedorRepo;
    private Cliente cliente;
    private Servicio servicio;
    @Autowired
    private ReservaRepository reservaRepository;

    @BeforeEach
    void setup() {
        // Limpio tablas
        servicioRepo.deleteAll();
        clienteRepo.deleteAll();
        User user = new User();
        user.setEmail("prov.a@example.com");
        user.setPassword("pwd123");
        // Creo un proveedor
        Proveedor prov = new Proveedor();
        prov.setNombre("ProvA");
        //prov.setEmail("prov.a@example.com");
        prov.setTelefono("987000111");
        prov.setDescripcion("Servicios varios");
        //prov.setPassword("pwd123");
        prov = proveedorRepo.save(prov);
        // Creo un cliente
        user.setEmail("ana.gomez@example.com");
        user.setPassword("secret");
        cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setApellido("Gómez");
        //cliente.setEmail("ana.gomez@example.com");
        cliente.setTelefono("912345678");
        //cliente.setPassword("secret");
        cliente = clienteRepo.save(cliente);
        // Creo un servicio asociado al proveedor
        Servicio svc = new Servicio();
        svc.setNombre("Servicio Test");
        svc.setDescripcion("Descripción Test");
        svc.setPrecio(new BigDecimal("100.00"));
        svc.setCategoria(Categorias.LIMPIEZA);
        svc.setProveedor(prov);
        servicio = servicioRepo.save(svc);
    }

    @Test
    void listarTodasReservas_sinDatos_retornaListaVacia() throws Exception {
        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void listarTodasReservas_conDatos_retornaListaDeReservas() throws Exception {
        // Crear una reserva en la base de datos
        ReservaDTO reserva = new ReservaDTO();
        reserva.setEstado(EstadoReserva.PENDIENTE);
        reserva.setFechaReserva(LocalDateTime.now());
        reserva.setDireccion("Av. Siempre Viva 742");

        // Crear una reserva en la base de datos
        Reserva reservaEntity = new Reserva();
        reservaEntity.setCliente(cliente);
        reservaEntity.setServicio(servicio);
        reservaEntity.setEstado(reserva.getEstado());
        reservaEntity.setFechaReserva(reserva.getFechaReserva());
        reservaEntity.setDireccion(reserva.getDireccion());
        reservaRepository.save(reservaEntity);

        mockMvc.perform(get("/api/reservas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$[0].servicioId").value(servicio.getId()))
                .andExpect(jsonPath("$[0].estado").value("PENDIENTE"))
                .andExpect(jsonPath("$[0].direccion").value("Av. Siempre Viva 742"));
    }
}
