package com.corgan.angularbackend.datamodels;

import lombok.Data;

import java.math.BigInteger;

@Data
public class CustomerRequest {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private boolean active;

    private BigInteger phone;

    private boolean privileges;

}
