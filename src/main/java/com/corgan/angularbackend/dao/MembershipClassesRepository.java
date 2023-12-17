package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.Classes;
import com.corgan.angularbackend.entity.CompositeKeyMembership;
import com.corgan.angularbackend.entity.Membership;
import com.corgan.angularbackend.entity.MembershipClasses;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MembershipClassesRepository extends JpaRepository<MembershipClasses, Long> {

    MembershipClasses findById(CompositeKeyMembership key);
    void deleteById(CompositeKeyMembership key);
    void deleteByMembership(Membership membership);

    void deleteByClasses(Classes classes);
}
