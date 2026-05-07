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
import java.util.List;
import java.util.Optional;

@Service
public class AppointmentServiceManager {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentServiceRepository appointmentServiceRepository;
    private final ServiceOfferingRepository serviceOfferingRepository;

    @Autowired
    public AppointmentServiceManager(
            AppointmentRepository appointmentRepository,
            AppointmentServiceRepository appointmentServiceRepository,
            ServiceOfferingRepository serviceOfferingRepository
    ) {
        this.appointmentRepository = appointmentRepository;
        this.appointmentServiceRepository = appointmentServiceRepository;
        this.serviceOfferingRepository = serviceOfferingRepository;
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

        for (Integer serviceId : serviceIds) {
            ServiceOffering serviceOffering = serviceOfferingRepository.findById(serviceId)
                    .orElseThrow(() -> new IllegalArgumentException("Servicio no existe"));
            AppointmentService link = new AppointmentService();
            link.setAppointment(saved);
            link.setService(serviceOffering);
            appointmentServiceRepository.save(link);
        }

        return new Message("Cita creada con exito", true);
    }
}
