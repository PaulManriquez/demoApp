package com.demoApp.demoApp.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class CreateAppointmentRequest {

    @NotNull(message = "La fecha es obligatoria")
    private LocalDate date;

    @NotNull(message = "La hora es obligatoria")
    private LocalTime time;

    @NotNull(message = "El cliente es obligatorio")
    private Integer clientId;

    @NotNull(message = "El tecnico es obligatorio")
    private Integer technicianUserId;

    @NotNull(message = "La duracion es obligatoria")
    @Min(value = 5, message = "La duracion minima es 5 minutos")
    private Integer durationMinutes = 60;

    private List<Integer> serviceIds;

    @NotBlank(message = "El estado es obligatorio")
    private String status = "CREATED";

    private String notes;
}

