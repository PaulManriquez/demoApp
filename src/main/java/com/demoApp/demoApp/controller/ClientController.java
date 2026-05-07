package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Client;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.AppointmentServiceManager;
import com.demoApp.demoApp.service.ClientService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/clients")
public class ClientController {

    private final ClientService clientService;
    private final AppointmentServiceManager appointmentServiceManager;

    @Autowired
    public ClientController(ClientService clientService, AppointmentServiceManager appointmentServiceManager) {
        this.clientService = clientService;
        this.appointmentServiceManager = appointmentServiceManager;
    }

    @GetMapping({"", "/"})
    public String showClientsPage(Model model) {
        model.addAttribute("client", new Client());
        model.addAttribute("clients", clientService.getAllClients());
        return "administration/clients/index";
    }

    @PostMapping({"", "/"})
    public String createClient(@Valid Client client, BindingResult result,
                               RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            model.addAttribute("clients", clientService.getAllClients());
            return "administration/clients/index";
        }

        Message message = clientService.createClient(client);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/clients/";
    }

    @PutMapping("/update")
    public String updateClient(@Valid Client client, BindingResult result,
                               RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            model.addAttribute("clients", clientService.getAllClients());
            return "administration/clients/index";
        }

        Message message = clientService.updateClient(client);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/clients/";
    }

    @GetMapping("/status/toggle/{id}")
    public String toggleStatus(@PathVariable("id") int id, RedirectAttributes attributes) {
        Message message = clientService.toggleActive(id);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/clients/";
    }

    @GetMapping("/{id}/history")
    public String clientHistory(@PathVariable("id") int id, Model model) {
        Client client = clientService.getClientById(id);
        model.addAttribute("client", client);
        model.addAttribute("appointments", appointmentServiceManager.getAllAppointmentsForClient(id));
        model.addAttribute("statusOrder", java.util.List.of("CREATED", "CONFIRMED", "COMPLETED", "CANCELED", "NO_SHOW"));
        model.addAttribute("position", "clients");
        return "administration/clients/history";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "clients");
    }

    private boolean hasValidationErrors(BindingResult result) {
        if (!result.hasErrors()) {
            return false;
        }
        for (ObjectError error : result.getAllErrors()) {
            org.slf4j.LoggerFactory.getLogger(getClass()).warn("Error: {}", error.getDefaultMessage());
        }
        return true;
    }
}
