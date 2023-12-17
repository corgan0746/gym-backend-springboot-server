package com.corgan.angularbackend.customdao;

import com.corgan.angularbackend.datamodels.MembershipClassesResponse;
import com.corgan.angularbackend.entity.Membership;

import java.util.List;

public interface MembershipClassesDAO {
    List<MembershipClassesResponse> returnAll(List<Membership> memberships);

    MembershipClassesResponse getById(int id);
}
