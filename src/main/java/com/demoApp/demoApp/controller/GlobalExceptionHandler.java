package com.demoApp.demoApp.controller;

import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.NoHandlerFoundException;

@ControllerAdvice
public class GlobalExceptionHandler {

    // HANDLE ERROR - 404
    @ExceptionHandler(NoHandlerFoundException.class)
    public String handle404(){
        return "redirect:/page-not-found";
    }

    // HANDLE ERROR VALIDATIONS  - 400
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public String handleValidation400(){
        return "redirect:/bad-request";
    }

    // HANDLE ERROR GENERAL BAD REQUEST  - 400
    @ExceptionHandler(IllegalArgumentException.class)
    public String handleBadRequest400(){
        return "redirect:/bad-request";
    }

}
