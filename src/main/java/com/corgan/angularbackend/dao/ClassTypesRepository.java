package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.ClassTypes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClassTypesRepository extends JpaRepository<ClassTypes, Long> {
}
