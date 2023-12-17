package com.corgan.angularbackend.datamodels;

import com.corgan.angularbackend.entity.Classes;
import com.corgan.angularbackend.entity.Customer;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class BookingRequest {

    private Long id;

    private BigDecimal value;

    private Date dateCreated;

    private boolean active;

    private boolean paid;

    private Long classes;

    private String reference;
}
