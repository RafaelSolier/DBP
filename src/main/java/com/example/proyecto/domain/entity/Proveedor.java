package com.example.proyecto.domain.entity;

import jakarta.persistence.*;
import java.util.List;

import lombok.Data;

import java.math.BigDecimal;

@Entity
@Data
@Table(name = "proveedores")
public class Proveedor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

//    @Column(nullable = false, unique = true)
//    private String email;

//    @Column(nullable = false)
//    private String password;

    @Column(length = 2000)
    private String descripcion;

    @Column(length = 100)
    private String telefono;

    @Column(nullable = false, precision = 2)
    private BigDecimal rating = BigDecimal.ZERO;

    @OneToMany(mappedBy = "proveedor", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Servicio> servicios;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    // Getters y setters
}