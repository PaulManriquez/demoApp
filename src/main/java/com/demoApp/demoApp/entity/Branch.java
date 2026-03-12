package com.demoApp.demoApp.entity;

import jakarta.persistence.*;
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

    @Column(name = "name")
    private String name;

    @Column(name = "address")
    private String address;

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