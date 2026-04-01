package com.demoApp.demoApp.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// Simple feedback object used to carry success or error messages to the UI layer.
@Getter
@Setter
@AllArgsConstructor
public class Message {

    private String body;
    private boolean success;
}
