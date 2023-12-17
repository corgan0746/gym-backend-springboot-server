package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "membership")
@Data
public class Membership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    @Column(name = "code")
    private String code;

    @Column(name = "start_date")
    private Date startDate;

    @Column(name = "expire_date")
    private Date expireDate;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "auto_renew")
    private boolean renew;

    @Column(name = "active")
    private boolean active;

    @Column(name = "image")
    private String image;


}
