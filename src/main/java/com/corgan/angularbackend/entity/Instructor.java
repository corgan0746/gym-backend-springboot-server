package com.corgan.angularbackend.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "instructor")
@Getter
@Setter
public class Instructor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;


    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Column(name = "email")
    private String email;

    @Column(name = "bios")
    private String bios;

    @Column(name = "image")
    private String image;



    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "instructor")
    private Set<Classes> classes = new HashSet<>();




    ////

    // Wrong one
//    @ManyToOne
//    @JoinColumn(name = "instructorId")
//    private InstructorClasses instructorClasses;

//    public void add(Classes class1){
//        if(class1 != null){
//            if(classesTeach ==null){
//                classesTeach = new HashSet<>();
//            }
//
//            classesTeach.add(class1);
////            class.setOrder(this);
//        }
//    }


}
