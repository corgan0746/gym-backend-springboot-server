package com.corgan.angularbackend.datamodels;

import lombok.Data;

@Data
public class InstructorRequest {

    private Long id;

    private String firstName;

    private String lastName;

    private String email;

    private String bios;

}
