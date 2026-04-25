package com.demoApp.demoApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin")
public class AdminPageController {

    @GetMapping
    public String showAdminHome(){
        return "administration/index";
    }

    @ModelAttribute
    public void setGenerics(Model model) {
        model.addAttribute("position", "dashboard");
    }
}
