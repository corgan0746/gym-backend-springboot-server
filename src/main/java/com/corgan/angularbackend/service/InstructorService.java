package com.corgan.angularbackend.service;

import com.corgan.angularbackend.customdao.InstructorDAOImpl;
import com.corgan.angularbackend.dao.InstructorRepository;
import com.corgan.angularbackend.entity.Instructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructorService {

    private InstructorRepository instructorRepository;
    private InstructorDAOImpl dao;


    public InstructorService(InstructorRepository instructorRepository, InstructorDAOImpl dao) {
        this.instructorRepository = instructorRepository;
        this.dao = dao;
    }

    public List<Instructor> instructorsWithClassesCountSort(){
        return dao.allInstructorsOrderByClassesCount();
    }

    public List<Instructor> instructorsWithClasses(){
        return dao.allInstructorsWithClasses();
    }

}
