package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.Instructor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.util.List;

@RepositoryRestResource(collectionResourceRel = "instructor", path = "instructor")
public interface InstructorRepository extends JpaRepository<Instructor, Long> {

    List<Instructor> findAll();

}