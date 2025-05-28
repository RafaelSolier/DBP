package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.WelcomeEmailEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class WelcomeEmailListener {

    final private EmailService emailService;

    public WelcomeEmailListener(EmailService emailService) {
        this.emailService = emailService;
    }

    @EventListener
    @Async
    public void sendWelcomeEmail(WelcomeEmailEvent welcomeEmailEvent) {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envío de email de registro fue interrumpido: " + e);
        }
        Map<String,Object> vars = new HashMap<>();
        vars.put("name", welcomeEmailEvent.getName());
        vars.put("email", welcomeEmailEvent.getEmail());
        emailService.sendWelcomeEmail(welcomeEmailEvent.getEmail(), vars);
    }
}
