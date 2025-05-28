package com.example.proyecto.email.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Autowired
    private SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        javaMailSender.send(message);
    }

    @Async
    public void sendHtmlEmail(String to, String subject, Map<String,Object> variables) {
        MimeMessage mime = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("ride-booked", context);
            helper.setText(html, true);
            // Incrustar imagen (opcional)
            ClassPathResource qr = new ClassPathResource("/static/images/qr-yape.png");
            helper.addInline("qr", qr);

            javaMailSender.send(mime);
        } catch (MessagingException e) {
            throw new MailSendException("Error al enviar correo HTML", e);
        }
    }

    @Async
    public void sendWelcomeEmail(String to, Map<String,Object> variables) {
        String subject =  "Bienvenido a ServiMatch";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("welcome_email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de bienvenida", e);
        }

    }

    @Async
    public void sendAcceptReservaEmail(String to, Map<String,Object> variables) {
        String subject = "Reserva aceptada";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("accept_email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de aceptación de reserva", e);
        }
    }

    @Async
    public void sendPaymentEmail(String to, Map<String,Object> variables) {
        String subject = "Pago realizado con éxito";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("payment_email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de pago exitoso", e);
        }
    }

    @Async
    public void sendCreateReservaEmail(String to, Map<String,Object> variables){
        String subject = "Reserva solicitada";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("solicitud-reserva-email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de solicitud de reserva", e);
        }
    }

    @Async
    public void sendCancelReservaEmail(String to, Map<String,Object> variables){
        String subject = "Reserva cancelada";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("cancel_email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de cancelación", e);
        }
    }

    @Async
    public void sendCompleteReservaEmail(String to, Map<String,Object> variables){
        String subject = "Servicio completado";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("complete_email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de servicio completado", e);
        }
    }

    @Async
    public void sendRejectReservaEmail(String to, Map<String,Object> variables){
        String subject = "Reserva rechazada";
        MimeMessage mime = javaMailSender.createMimeMessage();
        try{
            MimeMessageHelper helper = new MimeMessageHelper(mime, true, "UTF-8");
            // Datos básicos
            helper.setTo(to);
            helper.setSubject(subject);
            // Procesar plantilla
            Context context = new Context();
            context.setVariables(variables);
            String html = templateEngine.process("reject_email", context);
            helper.setText(html, true);
            javaMailSender.send(mime);
        }catch (MessagingException e){
            throw new MailSendException("Error al enviar correo de reserva rechazada", e);
        }
    }
}
