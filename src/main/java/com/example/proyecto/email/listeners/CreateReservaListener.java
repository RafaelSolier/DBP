package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.CreateReservaEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CreateReservaListener {
    final private EmailService emailService;
    public CreateReservaListener(EmailService emailService) {this.emailService = emailService;}

    @EventListener
    @Async
    public void sendCreateConfirmationEmail(CreateReservaEvent createReservaEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de Confirmación de creación de reserva fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("nameCliente", createReservaEvent.getNameCliente());
        vars.put("nameProveedor", createReservaEvent.getNameProveedor());
        vars.put("fecha", createReservaEvent.getFechaReserva());
        vars.put("direccion", createReservaEvent.getDireccion());
        vars.put("nombreServicio", createReservaEvent.getNombreServicio());
        emailService.sendCreateReservaEmail(createReservaEvent.getEmail(), vars);
    }
}
