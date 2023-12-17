package com.corgan.angularbackend.datamodels;

import com.corgan.angularbackend.entity.Classes;
import jakarta.persistence.Column;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class MembershipClassesResponse {
    private int id;
    private String name;
    private String code;
    private Date startDate;
    private Date expireDate;
    private BigDecimal value;
    private String image;
    private boolean renew;
    private boolean active;

    private Set<Classes> classes = new HashSet<>();

    public void addClass(Classes classes1){
        if(classes != null){
            if(classes == null){
                classes = new HashSet<>();
            }
            this.classes.add(classes1);
        }
    }
}
