package com.corgan.angularbackend.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "classes")
@Getter
@Setter
public class Classes {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="id")
    private int id;

    @Column(name = "code")
    private String code;

    @Column(name = "name")
    private String name;

    @Column(name = "value")
    private BigDecimal value;

    @Column(name = "start_time")
    private Date startTime;

    @Column(name = "end_time")
    private Date endTime;

    @Column(name = "description")
    private String description;

    @Column(name = "image")
    private String image;

    //Somehow it just fixed itself...

    //    @JsonManagedReference
    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "instructor_id")
    private Instructor instructor;


    @OneToOne
    @JoinColumn(name = "location_id")
    private Location location;


    @OneToOne
    @JoinColumn (name = "class_type", referencedColumnName = "id")
    @JsonManagedReference
    private ClassTypes classTypes;

    @JsonManagedReference
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classes")
    private Set<Timeslots> timeslots = new HashSet<>();


    public void add(Timeslots timeslot){
        if(timeslot != null){
            if(timeslots == null){
                timeslots = new HashSet<>();
            }
            timeslots.add(timeslot);
            timeslot.setClasses(this);
        }
    }


//    @OneToOne
//    @PrimaryKeyJoinColumn
//    private InstructorClasses instructorClasses;

//    @OneToMany(cascade = CascadeType.ALL, mappedBy = "classes")
//    private Set<InstructorClasses> instructorClasses = new HashSet<>();

    // Wrong one
//    @ManyToOne
//    @JoinColumn(name = "classId")
//    private InstructorClasses instructorClasses;

//    , referencedColumnName = "code"
//    @ManyToOne
//    @JoinColumn(name = "instructor_od")
//    private Instructor instructorId;





}
