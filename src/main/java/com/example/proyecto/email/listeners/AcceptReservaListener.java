package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.AcceptReservaEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class AcceptReservaListener {
    final private EmailService emailService;
    public AcceptReservaListener(EmailService emailService) {this.emailService = emailService;}

    @EventListener
    @Async
    public void sendAcceptReservaEmail(AcceptReservaEvent acceptReservaEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de aceptación de reserva fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("nameCliente", acceptReservaEvent.getNameCliente());
        vars.put("nameProveedor", acceptReservaEvent.getNameProveedor());
        vars.put("fecha", acceptReservaEvent.getFechaReserva());
        vars.put("nombreServicio", acceptReservaEvent.getNombreServicio());
        emailService.sendAcceptReservaEmail(acceptReservaEvent.getEmail(), vars);
    }
}
