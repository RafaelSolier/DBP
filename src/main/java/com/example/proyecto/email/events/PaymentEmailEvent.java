package com.example.proyecto.email.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class PaymentEmailEvent extends ApplicationEvent {
    private String nameCliente;
    private String email;
    private BigDecimal amount;
    private String nameProveedor;
    private LocalDateTime fechaReserva;
    private String direccion;
    private String nombreServicio;

    public PaymentEmailEvent(Object source, String nameCliente,
                             String email,
                             BigDecimal amount,
                             String nameProveedor,
                             LocalDateTime fechaReserva,
                             String direccion,
                             String nombreServicio) {
        super(source);
        this.nameCliente = nameCliente;
        this.email = email;
        this.amount = amount;
        this.nameProveedor = nameProveedor;
        this.fechaReserva = fechaReserva;
        this.direccion = direccion;
        this.nombreServicio = nombreServicio;
    }


}
