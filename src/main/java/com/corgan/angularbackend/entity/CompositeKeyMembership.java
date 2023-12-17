package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.io.Serializable;

@Embeddable
public class CompositeKeyMembership implements Serializable {
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "membership_id")
    @Column(name = "membership_id")
    private int membershipId;

//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "class_id")
    @Column(name = "class_id")
    private int classId;


    public CompositeKeyMembership() {
    }

    //Remove after
    public CompositeKeyMembership(int membershipId, int classesId) {
        this.membershipId = membershipId;
        this.classId = classesId;
    }

    public int getMembershipId() {
        return membershipId;
    }


    public void setMembershipId(int membershipId) {
        this.membershipId = membershipId;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassesId(int classesId) {
        this.classId = classesId;
    }
}




