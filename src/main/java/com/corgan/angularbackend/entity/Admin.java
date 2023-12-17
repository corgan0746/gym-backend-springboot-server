package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
@Entity
@Table(name = "adm")
@Data
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "username")
    private String username;

    @Column(name = "password")
    private String password;
}