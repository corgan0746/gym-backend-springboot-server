package com.corgan.angularbackend.service;

import com.corgan.angularbackend.customdao.MembershipClassesDAOImpl;
import com.corgan.angularbackend.dao.MembershipRepository;
import com.corgan.angularbackend.datamodels.MembershipClassesResponse;
import com.corgan.angularbackend.entity.Membership;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MembershipClassesService {

    private MembershipClassesDAOImpl dao;
    private MembershipRepository membershipRepository;

    public MembershipClassesService(
            MembershipClassesDAOImpl dao,
            MembershipRepository membershipRepository
    ) {
        this.dao = dao;
        this.membershipRepository = membershipRepository;
    }

    public List<MembershipClassesResponse> returnAllMembershipClasses(){

        List<Membership> memberships = membershipRepository.findAll();

        return dao.returnAll(memberships);
    }

    public void checkingData(){
        dao.checkingData();
    }

    public MembershipClassesResponse getMembershipWithClassesById(int id){
        return dao.getById(id);
    }



}
