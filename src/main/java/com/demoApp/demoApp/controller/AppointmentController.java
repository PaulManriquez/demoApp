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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

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

    @GetMapping("/agenda")
    public String agenda(
            @RequestParam(value = "date", required = false) LocalDate date,
            Model model
    ) {
        LocalDate target = date != null ? date : LocalDate.now();
        LocalDateTime start = target.atStartOfDay();
        LocalDateTime end = target.plusDays(1).atStartOfDay();

        model.addAttribute("date", target);
        model.addAttribute("appointments", appointmentServiceManager.getAgendaRange(start, end));
        model.addAttribute("statusOrder", List.of("CREATED", "CONFIRMED", "COMPLETED", "CANCELED", "NO_SHOW"));
        return "administration/appointments/agenda";
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
        if (Boolean.FALSE.equals(client.getActive())) {
            attributes.addFlashAttribute("msg", new Message("El cliente esta deshabilitado", false));
            return "redirect:/appointments?year=" + request.getDate().getYear() + "&month=" + request.getDate().getMonthValue();
        }
        User technician = userRepository.findById(request.getTechnicianUserId())
                .orElseThrow(() -> new IllegalArgumentException("Tecnico no existe"));
        if (!technician.isStatus()) {
            attributes.addFlashAttribute("msg", new Message("El tecnico esta deshabilitado", false));
            return "redirect:/appointments?year=" + request.getDate().getYear() + "&month=" + request.getDate().getMonthValue();
        }

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

    @PostMapping("/status/{id}")
    public String updateStatus(@PathVariable("id") int id,
                               @RequestParam("status") String status,
                               RedirectAttributes attributes,
                               @RequestParam(value = "redirect", required = false) String redirect) {
        AppointmentStatus nextStatus;
        try {
            nextStatus = AppointmentStatus.valueOf(status);
        } catch (IllegalArgumentException ex) {
            attributes.addFlashAttribute("msg", new Message("Estado invalido", false));
            return "redirect:/admin";
        }

        Message message = appointmentServiceManager.updateAppointmentStatus(id, nextStatus);
        attributes.addFlashAttribute("msg", message);

        if (redirect != null && !redirect.isBlank() && redirect.startsWith("/")) {
            return "redirect:" + redirect;
        }
        return "redirect:/admin";
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
        Map<String, Integer> countsByDay = new TreeMap<>();
        for (Map.Entry<LocalDate, List<Appointment>> entry : byDay.entrySet()) {
            countsByDay.put(entry.getKey().toString(), entry.getValue().size());
        }
        List<List<LocalDate>> weeks = buildWeeks(gridStart, gridEndExclusive.minusDays(1));

        model.addAttribute("yearMonth", yearMonth);
        model.addAttribute("weeks", weeks);
        model.addAttribute("appointmentsByDay", byDay);
        model.addAttribute("appointmentCountsByDay", countsByDay);
        model.addAttribute("today", today);
        model.addAttribute("dayNames", List.of("Lun", "Mar", "Mie", "Jue", "Vie", "Sab", "Dom"));
        model.addAttribute("todayAppointments", Optional.ofNullable(byDay.get(today)).orElseGet(List::of));

        model.addAttribute("request", request);
        model.addAttribute("clients", clientsRepository.findAllByActiveTrueOrderByFirstNameAscLastNameAsc());
        model.addAttribute("technicians", userRepository.findAllTechnicians());
        model.addAttribute("services", serviceOfferingService.getVisibleServicesForSelector());
        model.addAttribute("statuses", AppointmentStatus.values());
    }
}
