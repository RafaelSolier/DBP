package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.PaymentEmailEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class PaymentEmailListener {
    final private EmailService emailService;
    public PaymentEmailListener(EmailService emailService) {this.emailService = emailService;}

    @EventListener
    @Async
    public void sendPaymentEmail(PaymentEmailEvent paymentEmailEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de Pago fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("nameCliente", paymentEmailEvent.getNameCliente());
        vars.put("monto", paymentEmailEvent.getAmount());
        vars.put("nameProveedor", paymentEmailEvent.getNameProveedor());
        vars.put("fecha", paymentEmailEvent.getFechaReserva());
        vars.put("direccion", paymentEmailEvent.getDireccion());
        vars.put("nombreServicio", paymentEmailEvent.getNombreServicio());
        emailService.sendPaymentEmail(paymentEmailEvent.getEmail(), vars);
    }
}
