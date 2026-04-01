package com.demoApp.demoApp.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Entity
@Table(name = "branches")
@Getter
@Setter
public class Branch {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    // These constraints make @Valid in the controller reject incomplete branch data before persistence.
    @NotBlank(message = "El nombre de la sucursal es obligatorio")
    @Size(max = 100, message = "El nombre no puede exceder 100 caracteres")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "La direccion de la sucursal es obligatoria")
    @Size(max = 255, message = "La direccion no puede exceder 255 caracteres")
    @Column(name = "address")
    private String address;

    @NotBlank(message = "El enlace de Google Maps es obligatorio")
    @Size(max = 500, message = "El enlace no puede exceder 500 caracteres")
    @Column(name = "maps_link")
    private String mapsLink;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "active")
    private boolean active;

    @ManyToOne //      Column name of this table | id column name of User entity
    @JoinColumn(name = "user_id",referencedColumnName = "id")
    private  User user;

}
