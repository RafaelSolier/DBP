package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.CompleteReservaEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CompleteReservaListener {
    final private EmailService emailService;
    public CompleteReservaListener(EmailService emailService) {this.emailService = emailService;}

    @EventListener
    @Async
    public void sendCompleteReservaEmail(CompleteReservaEvent completeReservaEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de reserva completada fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("nameCliente", completeReservaEvent.getNameCliente());
        vars.put("nameProveedor", completeReservaEvent.getNameProveedor());
        vars.put("fecha", completeReservaEvent.getFechaReserva());
        vars.put("nombreServicio", completeReservaEvent.getNombreServicio());
        emailService.sendCompleteReservaEmail(completeReservaEvent.getEmail(), vars);
    }
}
