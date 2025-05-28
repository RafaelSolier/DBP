package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.RejectReservaEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class RejectReservaListener {
    final private EmailService emailService;
    public RejectReservaListener(EmailService emailService) {this.emailService = emailService;}

    @EventListener
    @Async
    public void sendRejectReservaEmail(RejectReservaEvent rejectReservaEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de rechazo de reserva fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("nameCliente", rejectReservaEvent.getNameCliente());
        vars.put("nameProveedor", rejectReservaEvent.getNameProveedor());
        vars.put("fecha", rejectReservaEvent.getFechaReserva());
        vars.put("nombreServicio", rejectReservaEvent.getNombreServicio());
        emailService.sendRejectReservaEmail(rejectReservaEvent.getEmail(), vars);
    }
}
