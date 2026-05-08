package com.demoApp.demoApp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String adminTo;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.adminTo:admin@example.com}") String adminTo) {
        this.mailSender = mailSender;
        this.adminTo = adminTo;
    }

    @Async
    public void sendAppointmentCreatedToAdmin(String subject, String body) {
        if (subject == null || subject.isBlank()) {
            subject = "Nueva cita creada";
        }
        if (body == null) {
            body = "";
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(adminTo);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
