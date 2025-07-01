package com.example.proyecto.domain.entity;

import com.example.proyecto.dto.ClienteResponseDto;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Table(name = "clientes")
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(nullable = false)
    private String telefono;

    private String foto;

    @Column(nullable = false)
    private LocalDateTime fechaRegistro = LocalDateTime.now();

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reserva> reservas;

    @OneToMany(mappedBy = "cliente", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resena> resenas;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;
    // getters y setters
    public ClienteResponseDto toResponseDto() {
        ClienteResponseDto responseDto = new ClienteResponseDto();
        responseDto.setId(this.getId());
        responseDto.setNombre(this.getNombre());
        responseDto.setApellido(this.getApellido());
        responseDto.setTelefono(this.getTelefono());
        return responseDto;
    }
}