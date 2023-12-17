package com.corgan.angularbackend.datamodels;

import lombok.Data;

import java.math.BigInteger;

@Data
public class RegistryPostRequest {

    private String firstName;

    private String lastName;

    private String email;

    private String password;

    private String phone;

    private String postcode;

    private String country;

    private String city;

    private String address;

}
