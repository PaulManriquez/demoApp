package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.ServiceCategory;
import com.demoApp.demoApp.entity.ServiceOffering;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.ServiceOfferingService;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/services")
public class ServiceOfferingController {

    private final ServiceOfferingService serviceOfferingService;

    @Autowired
    public ServiceOfferingController(ServiceOfferingService serviceOfferingService) {
        this.serviceOfferingService = serviceOfferingService;
    }

    @GetMapping({"", "/"})
    public String showServicesPage(Model model) {
        model.addAttribute("service", new ServiceOffering());
        model.addAttribute("services", serviceOfferingService.getAllServices());
        model.addAttribute("categories", ServiceCategory.values());
        return "administration/services/index";
    }

    @PostMapping({"", "/"})
    public String createService(@Valid ServiceOffering serviceOffering, BindingResult result,
                                RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            return reload(model, serviceOffering);
        }

        Message message = serviceOfferingService.createService(serviceOffering);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/services/";
    }

    @PutMapping("/update")
    public String updateService(@Valid ServiceOffering serviceOffering, BindingResult result,
                                RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            return reload(model, serviceOffering);
        }

        Message message = serviceOfferingService.updateService(serviceOffering);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/services/";
    }

    @GetMapping("/visibility/toggle/{id}")
    public String toggleVisibility(@PathVariable("id") int id, RedirectAttributes attributes) {
        Message message = serviceOfferingService.toggleVisibility(id);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/services/";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "services");
    }

    private String reload(Model model, ServiceOffering serviceOffering) {
        model.addAttribute("service", serviceOffering);
        model.addAttribute("services", serviceOfferingService.getAllServices());
        model.addAttribute("categories", ServiceCategory.values());
        return "administration/services/index";
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

