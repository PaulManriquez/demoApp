package com.demoApp.demoApp.controller;

import com.demoApp.demoApp.service.GoogleCalendarService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/google/oauth")
public class GoogleOAuthController {

    private final GoogleCalendarService googleCalendarService;

    public GoogleOAuthController(GoogleCalendarService googleCalendarService) {
        this.googleCalendarService = googleCalendarService;
    }

    @GetMapping("/start")
    public void start(HttpServletResponse response) throws IOException {
        response.sendRedirect(googleCalendarService.buildAuthorizationUrl());
    }

    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code,
                         HttpServletResponse response) throws IOException {
        googleCalendarService.handleOAuthCallback(code);
        response.sendRedirect("/admin");
    }
}

