package com.demoApp.demoApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "clients")
@Getter
@Setter
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 255, message = "El nombre no puede exceder 255 caracteres")
    @Column(name = "first_name", nullable = false, length = 255)
    private String firstName;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(max = 255, message = "El apellido no puede exceder 255 caracteres")
    @Column(name = "last_name", nullable = false, length = 255)
    private String lastName;

    @NotBlank(message = "La direccion es obligatoria")
    @Size(max = 255, message = "La direccion no puede exceder 255 caracteres")
    @Column(name = "address", nullable = false, length = 255)
    private String address;

    @NotBlank(message = "El maps link es obligatorio")
    @Size(max = 255, message = "El maps link no puede exceder 255 caracteres")
    @Column(name = "maps_link", nullable = false, length = 255)
    private String mapsLink;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 255, message = "El telefono no puede exceder 255 caracteres")
    @Column(name = "phone", nullable = false, length = 255)
    private String phone;

    @Column(name = "created_at", nullable = false, updatable = false, insertable = false)
    private Instant createdAt;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

}
