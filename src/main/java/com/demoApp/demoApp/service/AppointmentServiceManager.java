package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.Appointment;
import com.demoApp.demoApp.entity.AppointmentService;
import com.demoApp.demoApp.entity.AppointmentStatus;
import com.demoApp.demoApp.entity.ServiceOffering;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.AppointmentRepository;
import com.demoApp.demoApp.repository.AppointmentServiceRepository;
import com.demoApp.demoApp.repository.ServiceOfferingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceManager {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;
    private final EmailService emailService;
    private final GoogleCalendarService googleCalendarService;

    @Autowired
    public AppointmentServiceManager(
            AppointmentRepository appointmentRepository,
            AppointmentServiceRepository appointmentServiceRepository,
            ServiceOfferingRepository serviceOfferingRepository,
            EmailService emailService,
            GoogleCalendarService googleCalendarService
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
        this.emailService = emailService;
        this.googleCalendarService = googleCalendarService;
    }

    public List<Appointment> getCalendarRange(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return appointmentRepository.findForCalendarRange(startInclusive, endExclusive);
    }

    public List<Appointment> getAgendaRange(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return appointmentRepository.findForAgendaRangeWithDetails(startInclusive, endExclusive);
    }

    public List<Appointment> getAgendaRangeForTechnician(LocalDateTime startInclusive, LocalDateTime endExclusive, int technicianUserId) {
        return appointmentRepository.findForAgendaRangeWithDetailsForTechnician(startInclusive, endExclusive, technicianUserId);
    }

    public Optional<Appointment> getNextAppointment(LocalDateTime now) {
        List<Appointment> items = appointmentRepository.findNextActiveAppointments(
                now,
                List.of(AppointmentStatus.CREATED, AppointmentStatus.CONFIRMED)
        );
        return items.isEmpty() ? Optional.empty() : Optional.of(items.get(0));
    }

    public List<Appointment> getUpcomingAppointments(LocalDateTime startInclusive, LocalDateTime endExclusive) {
        return appointmentRepository.findUpcomingForRangeWithDetails(
                startInclusive,
                endExclusive,
                // Dashboard view allows changing to any status; keep the item visible after updates.
                List.of(
                        AppointmentStatus.CREATED,
                        AppointmentStatus.CONFIRMED,
                        AppointmentStatus.CANCELED,
                        AppointmentStatus.COMPLETED,
                        AppointmentStatus.NO_SHOW
                )
        );
    }

    public List<Appointment> getAllAppointmentsForClient(int clientId) {
        return appointmentRepository.findAllForClientWithDetails(clientId);
    }

    @Transactional
    public Message updateAppointmentStatus(int appointmentId, AppointmentStatus status) {
        Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new IllegalArgumentException("La cita no existe"));
        appointment.setStatus(status);
        appointmentRepository.save(appointment);
        return new Message("Estado de la cita actualizado", true);
    }

    @Transactional
    public Message createAppointment(Appointment appointment, List<Integer> serviceIds) {
        if (appointment.getId() != null) {
            return new Message("La cita ya existe", false);
        }

        if (appointment.getStartAt() == null || appointment.getEndAt() == null) {
            return new Message("Fecha/hora invalida", false);
        }

        if (!appointment.getEndAt().isAfter(appointment.getStartAt())) {
            return new Message("La hora fin debe ser mayor a la hora inicio", false);
        }

        if (serviceIds == null || serviceIds.isEmpty()) {
            return new Message("Selecciona al menos un servicio", false);
        }

        List<Appointment> overlaps = appointmentRepository.findOverlaps(
                appointment.getTechnician().getId(),
                appointment.getStartAt(),
                appointment.getEndAt(),
                AppointmentStatus.CANCELED
        );
        if (!overlaps.isEmpty()) {
            return new Message("El tecnico ya tiene una cita en ese horario", false);
        }

        Appointment saved = appointmentRepository.save(appointment);

        StringBuilder serviceNames = new StringBuilder();
        for (Integer serviceId : serviceIds) {
            ServiceOffering serviceOffering = serviceOfferingRepository.findById(serviceId)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no existe"));
            AppointmentService link = new AppointmentService();
            link.setAppointment(saved);
            link.setService(serviceOffering);
            appointmentServiceRepository.save(link);

            if (!serviceNames.isEmpty()) {
                serviceNames.append(", ");
            }
            serviceNames.append(serviceOffering.getName());
        }

        // =============================================================================================================
        // ( Get the current authenticated user before loose context in the Async method |
        //                                                                emailService.sendAppointmentCreatedToAdmin())
        // Get the current security context and Send email notification after the appointment is fully created.
        String createdBy = org.springframework.security.core.context.SecurityContextHolder.getContext() != null
                && org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication() != null
                ? org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication().getName()
                : "(desconocido)";

        // =============================================================================================================

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String clientName = saved.getClient() != null
                ? (saved.getClient().getFirstName() + " " + saved.getClient().getLastName())
                : "(sin cliente)";
        String techName = saved.getTechnician() != null
                ? (saved.getTechnician().getName() + " " + saved.getTechnician().getLastName())
                : "(sin tecnico)";

        String subject = "Nueva cita creada";
        String body = "Se creo una nueva cita.\n\n"
                + "Fecha/hora: " + (saved.getStartAt() != null ? saved.getStartAt().format(dtf) : "(sin fecha)") + "\n"
                + "Cliente: " + clientName + "\n"
                + "Tecnico que atendera: " + techName + "\n"
                + "Status de cita: " + (saved.getStatus() != null ? saved.getStatus().name() : "(sin status)") + "\n"
                + "Usuario quien creo la cita: " + createdBy + "\n";

        // Build iCalendar invite (RFC 5545). Gmail will show this as a calendar invitation.
        ZoneId zone = ZoneId.systemDefault();
        ZonedDateTime startZdt = saved.getStartAt() != null ? saved.getStartAt().atZone(zone) : null;
        ZonedDateTime endZdt = saved.getEndAt() != null ? saved.getEndAt().atZone(zone) : null;
        DateTimeFormatter icsFmt = DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmmss'Z'");
        String dtStampUtc = ZonedDateTime.now(ZoneOffset.UTC).format(icsFmt);
        String dtStartUtc = startZdt != null ? startZdt.withZoneSameInstant(ZoneOffset.UTC).format(icsFmt) : "";
        String dtEndUtc = endZdt != null ? endZdt.withZoneSameInstant(ZoneOffset.UTC).format(icsFmt) : "";

        String uid = "appointment-" + saved.getId() + "@demoApp";
        String summary = "Cita: " + clientName;
        String description = ("Cliente: " + clientName + "\\n"
                + "Tecnico: " + techName + "\\n"
                + "Servicios: " + (serviceNames.isEmpty() ? "-" : serviceNames) + "\\n"
                + "Creada por: " + createdBy + "\\n"
                + "Status: " + (saved.getStatus() != null ? saved.getStatus().name() : "-")
        ).replace("\n", "\\n");

        String ics = "BEGIN:VCALENDAR\r\n"
                + "PRODID:-//demoApp//Appointments//ES\r\n"
                + "VERSION:2.0\r\n"
                + "CALSCALE:GREGORIAN\r\n"
                + "METHOD:REQUEST\r\n"
                + "BEGIN:VEVENT\r\n"
                + "UID:" + uid + "\r\n"
                + "DTSTAMP:" + dtStampUtc + "\r\n"
                + (dtStartUtc.isBlank() ? "" : "DTSTART:" + dtStartUtc + "\r\n")
                + (dtEndUtc.isBlank() ? "" : "DTEND:" + dtEndUtc + "\r\n")
                + "SUMMARY:" + summary + "\r\n"
                + "DESCRIPTION:" + description + "\r\n"
                + "END:VEVENT\r\n"
                + "END:VCALENDAR\r\n";

        emailService.sendAppointmentCreatedInviteToAdmin(subject, body, ics);
        // Also insert directly into the admin's Google Calendar (requires OAuth connect first).
        googleCalendarService.createEventForAppointment(saved, createdBy);

        return new Message("Cita creada con exito", true);
    }
}
