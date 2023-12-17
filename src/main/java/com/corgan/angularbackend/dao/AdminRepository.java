package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    @Override
    Optional<Admin> findById(Long aLong);

    @Override
    <S extends Admin> S save(S entity);

    List<Admin> findByUsername(String username);
}
