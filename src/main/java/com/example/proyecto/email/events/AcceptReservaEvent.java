package com.example.proyecto.email.events;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.time.LocalDateTime;

@Getter
public class AcceptReservaEvent extends ApplicationEvent {
    private String nameCliente;
    private String email;
    private String nameProveedor;
    private LocalDateTime fechaReserva;
    private String nombreServicio;

    public AcceptReservaEvent(Object source, String nameCliente,
                              String email,
                              String nameProveedor,
                              LocalDateTime fechaReserva,
                              String nombreServicio) {
        super(source);
        this.nameCliente = nameCliente;
        this.email = email;
        this.nameProveedor = nameProveedor;
        this.fechaReserva = fechaReserva;
        this.nombreServicio = nombreServicio;
    }
}
