package com.demoApp.demoApp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class ViewController {

    @GetMapping("/login")
    public String loginPage(){
        return "login";
    }

    @GetMapping("/")
    public String root() {return "redirect:/home";}

    @GetMapping("/home")
    public String homePage(){
        return "home";
    }
}
