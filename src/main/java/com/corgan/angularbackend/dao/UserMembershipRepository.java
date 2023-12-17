package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.UserMembership;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface UserMembershipRepository extends JpaRepository<UserMembership, Long> {
    @Override
    <S extends UserMembership> S save(S entity);

    @Override
    <S extends UserMembership> List<S> saveAll(Iterable<S> entities);

    @Override
    void deleteById(Long aLong);

    @Override
    void delete(UserMembership entity);

    List<UserMembership> findAllByEndDateLessThan(Long date);

}
