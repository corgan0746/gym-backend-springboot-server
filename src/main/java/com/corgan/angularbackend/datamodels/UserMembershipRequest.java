package com.corgan.angularbackend.datamodels;

import com.corgan.angularbackend.entity.Membership;
import jakarta.persistence.*;
import lombok.Data;

@Data
public class UserMembershipRequest {

    private Long id;

    private boolean active;

    private boolean paid;

    private Long endDate;

    private boolean renew;

    private Long membership;

    private String reference;

}
