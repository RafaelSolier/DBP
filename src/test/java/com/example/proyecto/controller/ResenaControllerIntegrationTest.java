package com.example.proyecto.controller;
import com.example.proyecto.domain.entity.*;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.dto.ResenaRequestDto;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ResenaRepository;
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
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = NONE)
@Transactional
class ResenaControllerIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper mapper;
    @Autowired private ClienteRepository clienteRepo;
    @Autowired private ProveedorRepository proveedorRepo;
    @Autowired private ServicioRepository servicioRepo;
    @Autowired private ResenaRepository resenaRepo;

    private Cliente cliente;
    private Servicio servicio;

    @BeforeEach
    void setup() {
        // Limpio tablas
        resenaRepo.deleteAll();
        servicioRepo.deleteAll();
        clienteRepo.deleteAll();
        proveedorRepo.deleteAll();
        //Creo un User
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

        // Creo un servicio asociado al proveedor
        Servicio svc = new Servicio();
        svc.setNombre("Servicio Test");
        svc.setDescripcion("Descripción Test");
        svc.setPrecio(new BigDecimal("100.00"));
        svc.setCategoria(Categorias.LIMPIEZA);
        svc.setProveedor(prov);
        servicio = servicioRepo.save(svc);
        user.setEmail("ana.gomez@example.com");
        user.setPassword("secret");
        // Creo un cliente
        cliente = new Cliente();
        cliente.setNombre("Ana");
        cliente.setApellido("Gómez");
        //cliente.setEmail("ana.gomez@example.com");
        cliente.setTelefono("912345678");
        //cliente.setPassword("secret");
        cliente = clienteRepo.save(cliente);
    }

    @Test
    void crearResena_conDatosValidos_retorna201YGuarda() throws Exception {
        ResenaRequestDto dto = new ResenaRequestDto();
        dto.setClienteId(cliente.getId());
        dto.setServicioId(servicio.getId());
        dto.setCalificacion(5);
        dto.setFecha(LocalDateTime.now());
        dto.setComentario("Excelente servicio");

        mockMvc.perform(post("/api/resenas")
                        .contentType(APPLICATION_JSON)
                        .content(mapper.writeValueAsString(dto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.clienteId").value(cliente.getId()))
                .andExpect(jsonPath("$.servicioId").value(servicio.getId()))
                .andExpect(jsonPath("$.calificacion").value(5))
                .andExpect(jsonPath("$.comentario").value("Excelente servicio"))
                .andExpect(jsonPath("$.fecha").exists());

        assertThat(resenaRepo.findAll()).hasSize(1);
    }

    @Test
    void obtenerResenas_porServicioSinResenas_retornaListaVacia() throws Exception {
        mockMvc.perform(get("/api/servicios/{servicioId}/resenas", servicio.getId()))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void obtenerResenas_porServicioConResenas_retornaListado() throws Exception {
        // Creo directamente una reseña en BD
        Resena r = new Resena();
        r.setCliente(cliente);
        r.setServicio(servicio);
        r.setCalificacion(4);
        r.setComentario("Muy bueno");
        r.setFecha(LocalDateTime.now());
        resenaRepo.save(r);

        mockMvc.perform(get("/api/servicios/{servicioId}/resenas", servicio.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].comentario").value("Muy bueno"))
                .andExpect(jsonPath("$[0].calificacion").value(4));
    }
}
