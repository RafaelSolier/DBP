package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.CancelReservaEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CancelReservaListener {
    final private EmailService emailService;
    public CancelReservaListener(EmailService emailService) {this.emailService = emailService;}

    @EventListener
    @Async
    public void sendCancelReservaEmail(CancelReservaEvent cancelReservaEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de Cancelación de reserva fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("nameCliente", cancelReservaEvent.getNameCliente());
        vars.put("nameProveedor", cancelReservaEvent.getNameProveedor());
        vars.put("fecha", cancelReservaEvent.getFechaReserva());
        vars.put("nombreServicio", cancelReservaEvent.getNombreServicio());
        emailService.sendCancelReservaEmail(cancelReservaEvent.getEmail(), vars);
    }
}
