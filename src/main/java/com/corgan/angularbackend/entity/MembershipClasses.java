package com.corgan.angularbackend.entity;

import jakarta.persistence.*;
import lombok.Data;




@Entity
@Table(name = "membership_classes")
@Data
public class MembershipClasses {

//    @Id
//    @EmbeddedId
//    private CompositeKeyMembership compositeKeyMembership;
//
//    public MembershipClasses(int existingMembershipId, int existingClassesId) {
//        this.compositeKeyMembership = new CompositeKeyMembership();
//        this.compositeKeyMembership.setMembershipId(existingMembershipId);
//        this.compositeKeyMembership.setClassesId(existingClassesId);
//    }
//
    public MembershipClasses() {
    }

    @EmbeddedId
    private CompositeKeyMembership id;

    @ManyToOne
    @MapsId("membershipId")
    private Membership membership;

    @ManyToOne
    @MapsId("classId")
    @JoinColumn(name = "class_id")
    private Classes classes;

    public MembershipClasses(Membership membership, Classes classes) {
        this.membership = membership;
        this.classes = classes;
        this.id = new CompositeKeyMembership(membership.getId(), classes.getId());
    }



}
