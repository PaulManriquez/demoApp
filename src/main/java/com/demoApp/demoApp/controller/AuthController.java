package com.demoApp.demoApp.controller;


import com.demoApp.demoApp.dto.LoginRequest;
import com.demoApp.demoApp.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest payloadLoginRequest){

        boolean valid = authService.login(payloadLoginRequest.getUsername(), payloadLoginRequest.getPassword());

        if(!valid)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("The credentias do not match");

        return ResponseEntity.ok("Loggin success");
    }

}
