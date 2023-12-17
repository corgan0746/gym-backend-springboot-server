package com.corgan.angularbackend.datamodels;

import com.corgan.angularbackend.entity.ClassTypes;
import com.corgan.angularbackend.entity.Instructor;
import com.corgan.angularbackend.entity.Location;
import com.corgan.angularbackend.entity.Timeslots;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
public class ClassesRequest {

    private Long id;

    private String code;

    private String name;

    private BigDecimal value;

    private String description;

    private Long instructor;

    private Long location;

    private Long classTypes;

}
