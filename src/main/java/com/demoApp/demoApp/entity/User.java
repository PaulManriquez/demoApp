package com.demoApp.demoApp.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name="users")
@Getter
@Setter
public class User {

    @Id
    private int id;

    private String name;

    private String last_name;

    private String username;

    private String password;

    private String phone;

    private String email;

    private LocalDateTime startTime;

    //The database automatically sets the value | JPA should NOT try to insert/update it
    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    private boolean status;
}
