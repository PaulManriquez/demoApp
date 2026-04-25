package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.dto.UserForm;
import com.demoApp.demoApp.model.Message;
import com.demoApp.demoApp.service.UserAdminService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/users")
public class UserAdminController {

    private final UserAdminService userAdminService;

    @Autowired
    public UserAdminController(UserAdminService userAdminService) {
        this.userAdminService = userAdminService;
    }

    @GetMapping({"", "/"})
    public String showUsers(Model model) {
        model.addAttribute("userForm", new UserForm());
        model.addAttribute("users", userAdminService.getAllUsers());
        model.addAttribute("roles", userAdminService.getAllRoles());
        return "administration/users/index";
    }

    @PostMapping({"", "/"})
    public String createUser(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            return reload(model, userForm);
        }

        Message message = userAdminService.createUser(userForm);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/users/";
    }

    @PutMapping("/update")
    public String updateUser(@Valid @ModelAttribute("userForm") UserForm userForm, BindingResult result,
                             RedirectAttributes attributes, Model model) {
        if (hasValidationErrors(result)) {
            return reload(model, userForm);
        }

        Message message = userAdminService.updateUser(userForm);
        attributes.addFlashAttribute("msg", message);
        return "redirect:/users/";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "users");
    }

    private String reload(Model model, UserForm userForm) {
        model.addAttribute("userForm", userForm);
        model.addAttribute("users", userAdminService.getAllUsers());
        model.addAttribute("roles", userAdminService.getAllRoles());
        return "administration/users/index";
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

