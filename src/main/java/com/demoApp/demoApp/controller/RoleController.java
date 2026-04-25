package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.entity.Role;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.repository.RoleRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/roles")
public class RoleController {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleController(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @GetMapping({"", "/"})
    public String showRoles(Model model) {
        model.addAttribute("role", new Role());
        model.addAttribute("roles", roleRepository.findAll());
        return "administration/roles/index";
    }

    @PostMapping({"", "/"})
    public String createRole(@Valid Role role, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            model.addAttribute("roles", roleRepository.findAll());
            return "administration/roles/index";
        }

        try {
            roleRepository.save(role);
            attributes.addFlashAttribute("msg", new Message("Rol creado con exito", true));
        } catch (DataIntegrityViolationException ex) {
            attributes.addFlashAttribute("msg", new Message("El rol ya existe", false));
        }

        return "redirect:/roles/";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "roles");
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

