package com.demoApp.demoApp.service;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final String adminTo;
    private final String mailFrom;

    public EmailService(JavaMailSender mailSender,
                        @Value("${app.mail.adminTo:admin@example.com}") String adminTo,
                        @Value("${spring.mail.username:dummyEmail@example.com}") String mailFrom) {
        this.mailSender = mailSender;
        this.adminTo = adminTo;
        this.mailFrom = mailFrom;
    }

    @Async
    public void sendAppointmentCreatedInviteToAdmin(String subject, String body, String ics) {
        if (subject == null || subject.isBlank()) {
            subject = "Nueva cita creada";
        }
        if (body == null) {
            body = "";
        }
        if (ics == null) {
            ics = "";
        }

        try {
            MimeMessage message = mailSender.createMimeMessage();
            // multipart=true so we can attach the calendar invite
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(adminTo);
            helper.setFrom(mailFrom);
            helper.setSubject(subject);
            helper.setText(body, false);

            // Gmail recognizes this as a calendar invite when sent as text/calendar (REQUEST).
            byte[] icsBytes = ics.getBytes(StandardCharsets.UTF_8);
            ByteArrayDataSource dataSource = new ByteArrayDataSource(
                    icsBytes,
                    "text/calendar; charset=UTF-8; method=REQUEST"
            );
            helper.addAttachment("invite.ics", dataSource);

            mailSender.send(message);
        } catch (Exception ex) {
            // Email should not break appointment creation flow.
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("Failed to send appointment invite email", ex);
        }
    }
}
