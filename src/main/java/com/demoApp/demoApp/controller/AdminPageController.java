package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Appointment;
import com.demoApp.demoApp.entity.AppointmentStatus;
import com.demoApp.demoApp.service.AppointmentServiceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    private final AppointmentServiceManager appointmentServiceManager;

    @Autowired
    public AdminPageController(AppointmentServiceManager appointmentServiceManager) {
        this.appointmentServiceManager = appointmentServiceManager;
    }

    @GetMapping
    public String showAdminHome(Model model){
        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.plusDays(1).atStartOfDay();

        List<Appointment> todayAppointments = appointmentServiceManager.getAgendaRange(start, end);
        Map<String, Long> counts = buildCounts(todayAppointments);

        LocalDateTime upcomingStart = start;
        LocalDateTime upcomingEnd = today.plusDays(4).atStartOfDay();
        List<Appointment> upcomingAppointments = appointmentServiceManager.getUpcomingAppointments(upcomingStart, upcomingEnd);

        long disabledClients = todayAppointments.stream()
                .filter(a -> a.getClient() != null && Boolean.FALSE.equals(a.getClient().getActive()))
                .count();
        long disabledTechnicians = todayAppointments.stream()
                .filter(a -> a.getTechnician() != null && !a.getTechnician().isStatus())
                .count();
        long missingServices = todayAppointments.stream()
                .filter(a -> a.getServices() == null || a.getServices().isEmpty())
                .count();

        model.addAttribute("today", today);
        model.addAttribute("todayAppointments", todayAppointments);
        model.addAttribute("todayCounts", counts);
        model.addAttribute("statusOrder", List.of("CREATED", "CONFIRMED", "COMPLETED", "CANCELED", "NO_SHOW"));
        model.addAttribute("upcomingAppointments", upcomingAppointments);
        model.addAttribute("upcomingDays", 3);
        model.addAttribute("alertDisabledClients", disabledClients);
        model.addAttribute("alertDisabledTechnicians", disabledTechnicians);
        model.addAttribute("alertMissingServices", missingServices);

        return "administration/index";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "dashboard");
    }

    private Map<String, Long> buildCounts(List<Appointment> appointments) {
        Map<String, Long> counts = new LinkedHashMap<>();
        for (AppointmentStatus status : AppointmentStatus.values()) {
            long count = appointments.stream()
                    .filter(a -> a.getStatus() == status)
                    .count();
            counts.put(status.name(), count);
        }
        counts.put("TOTAL", (long) appointments.size());
        return counts;
    }
}
