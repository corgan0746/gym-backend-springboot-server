package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "location")
@Data
public class Location {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "direction")
    private String direction;

    @Column(name = "postcode")
    private String postcode;

    @Column(name = "city")
    private String city;

    @Column(name = "phone_number")
    private String phone;

    @Column(name = "image")
    private String image;

//    @OneToOne(targetEntity = Classes.class, cascade = CascadeType.ALL, mappedBy = "location")
//    @Column(name = "name")
//    private String name;

}
