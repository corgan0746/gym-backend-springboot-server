package com.corgan.angularbackend.customdao;

import com.corgan.angularbackend.entity.Instructor;

import java.util.List;

public interface InstructorDAO {

    List<Instructor> allInstructorsOrderByClassesCount();

    List<Instructor> allInstructorsWithClasses();

    void save(Instructor instructor);

}
