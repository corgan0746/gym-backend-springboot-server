package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigInteger;

@Entity
@Table(name = "user_membership")
@Data
public class UserMembership {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY )
    @Column(name = "id")
    private int id;

    @Column(name = "active")
    private boolean active;

    @Column(name = "paid")
    private boolean paid;

    @Column(name = "end_date")
    private Long endDate;

//    @Column(name = "payment_url")
//    private String paymentUrl;

    @Column(name = "renew")
    private boolean renew;

    @OneToOne
    @JoinColumn(name = "membership_id", referencedColumnName = "id")
    private Membership membership;

    //Added new 15/10/2023
    @Column(name = "reference")
    private String reference;

}
