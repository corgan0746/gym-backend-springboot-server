package com.corgan.angularbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "class_types")
@Data
public class ClassTypes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

//    @OneToOne
//    @PrimaryKeyJoinColumn
//    @JsonBackReference
//    private Classes classes;

}
