package com.corgan.angularbackend.datamodels;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Data
public class MembershipRequest {

    private Long id;

    private String name;

    private String code;

    private BigDecimal value;

    private boolean renew;

    private boolean active;

    private Long classId;

}
