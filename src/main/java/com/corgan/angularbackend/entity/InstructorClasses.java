package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "instructor_classes")
@Data
public class InstructorClasses {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

//    @Column(name = "instructor_id")
//    private int instructorId;
//
//    @Column(name = "class_id")
//    private int classId;



    ///






    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;


//    @ManyToOne
//    @JoinColumn(name = "instructor_id", referencedColumnName = "id")
//    private Instructor instructor;


    ///


    @ManyToOne
    @JoinColumn(name = "class_id")
    private Classes classes;

}
