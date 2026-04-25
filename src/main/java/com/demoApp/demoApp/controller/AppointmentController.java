package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.dto.CreateAppointmentRequest;
import com.demoApp.demoApp.entity.Appointment;
import com.demoApp.demoApp.entity.AppointmentStatus;
import com.demoApp.demoApp.entity.Client;
import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.ClientsRepository;
import com.demoApp.demoApp.repository.UserRepository;
import com.demoApp.demoApp.service.AppointmentServiceManager;
import com.demoApp.demoApp.service.ServiceOfferingService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/appointments")
public class AppointmentController {

    private final AppointmentServiceManager appointmentServiceManager;
    private final ClientsRepository clientsRepository;
    private final UserRepository userRepository;
    private final ServiceOfferingService serviceOfferingService;

    @Autowired
    public AppointmentController(
            AppointmentServiceManager appointmentServiceManager,
            ClientsRepository clientsRepository,
            UserRepository userRepository,
            ServiceOfferingService serviceOfferingService
    ) {
        this.appointmentServiceManager = appointmentServiceManager;
        this.clientsRepository = clientsRepository;
        this.userRepository = userRepository;
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping({"", "/"})
    public String calendar(
            @RequestParam(value = "year", required = false) Integer year,
            @RequestParam(value = "month", required = false) Integer month,
            Model model
    ) {
        populateCalendarModel(year, month, model, new CreateAppointmentRequest());
        return "administration/appointments/index";
    }

    @PostMapping({"", "/"})
    public String create(@Valid @ModelAttribute("request") CreateAppointmentRequest request,
                         BindingResult result,
                         RedirectAttributes attributes,
                         Model model) {

        if (result.hasErrors()) {
            Integer year = request.getDate() != null ? request.getDate().getYear() : null;
            Integer month = request.getDate() != null ? request.getDate().getMonthValue() : null;
            populateCalendarModel(year, month, model, request);
            return "administration/appointments/index";
        }

        Client client = clientsRepository.findById(request.getClientId())
                .orElseThrow(() -> new IllegalArgumentException("Cliente no existe"));
        User technician = userRepository.findById(request.getTechnicianUserId())
                .orElseThrow(() -> new IllegalArgumentException("Tecnico no existe"));

        LocalDateTime startAt = LocalDateTime.of(request.getDate(), request.getTime());
        LocalDateTime endAt = startAt.plusMinutes(request.getDurationMinutes());

        Appointment appointment = new Appointment();
        appointment.setClient(client);
        appointment.setTechnician(technician);
        appointment.setStartAt(startAt);
        appointment.setEndAt(endAt);
        appointment.setNotes(request.getNotes());
        appointment.setStatus(AppointmentStatus.valueOf(request.getStatus()));

        Message message = appointmentServiceManager.createAppointment(appointment, request.getServiceIds());
        attributes.addFlashAttribute("msg", message);

        return "redirect:/appointments?year=" + request.getDate().getYear() + "&month=" + request.getDate().getMonthValue();
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "appointments");
    }

    private static Map<LocalDate, List<Appointment>> groupByDay(List<Appointment> appointments) {
        Map<LocalDate, List<Appointment>> byDay = new LinkedHashMap<>();
        for (Appointment appointment : appointments) {
            LocalDate day = appointment.getStartAt().toLocalDate();
            byDay.computeIfAbsent(day, ignored -> new ArrayList<>()).add(appointment);
        }
        return byDay;
    }

    private static List<List<LocalDate>> buildWeeks(LocalDate start, LocalDate endInclusive) {
        List<List<LocalDate>> weeks = new ArrayList<>();
        LocalDate cursor = start;
        while (!cursor.isAfter(endInclusive)) {
            List<LocalDate> week = new ArrayList<>(7);
            for (int i = 0; i < 7; i++) {
                week.add(cursor);
                cursor = cursor.plusDays(1);
            }
            weeks.add(week);
        }
        return weeks;
    }

    private void populateCalendarModel(Integer year, Integer month, Model model, CreateAppointmentRequest request) {
        LocalDate today = LocalDate.now();
        int y = year != null ? year : today.getYear();
        int m = month != null ? month : today.getMonthValue();

        YearMonth yearMonth = YearMonth.of(y, m);
        LocalDate first = yearMonth.atDay(1);
        LocalDate last = yearMonth.atEndOfMonth();

        LocalDate gridStart = first.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate gridEndExclusive = last.with(TemporalAdjusters.next(DayOfWeek.SUNDAY)).plusDays(1);

        LocalDateTime rangeStart = gridStart.atStartOfDay();
        LocalDateTime rangeEnd = gridEndExclusive.atStartOfDay();

        List<Appointment> appointments = appointmentServiceManager.getCalendarRange(rangeStart, rangeEnd);
        Map<LocalDate, List<Appointment>> byDay = groupByDay(appointments);
        List<List<LocalDate>> weeks = buildWeeks(gridStart, gridEndExclusive.minusDays(1));

        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("weeks", weeks);
        model.addAttribute("appointmentsByDay", byDay);
        model.addAttribute("today", today);

        model.addAttribute("request", request);
        model.addAttribute("clients", clientsRepository.findAll());
        model.addAttribute("technicians", userRepository.findAllTechnicians());
        model.addAttribute("services", serviceOfferingService.getVisibleServicesForSelector());
        model.addAttribute("statuses", AppointmentStatus.values());
    }
}
