package com.corgan.angularbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;

@Entity
@Table(name = "bookings")
@Data
public class Bookings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "date_created")
    private Date dateCreated;

    @Column(name = "active")
    private boolean active;

    @Column(name = "paid")
    private boolean paid;

//    @Column(name = "payment_url")
//    private String paymentUrl;

    //    @JsonManagedReference
    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classes;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private Customer customer;

    //Added new 15/10/2023
    @Column(name = "reference")
    private String reference;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "timeslot_id")
    private Timeslots timeslot;



}
