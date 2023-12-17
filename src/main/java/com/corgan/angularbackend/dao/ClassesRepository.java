package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.ClassTypes;
import com.corgan.angularbackend.entity.Classes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

import java.math.BigDecimal;
import java.util.List;

@RepositoryRestResource(collectionResourceRel = "classes", path = "classes")
public interface ClassesRepository extends JpaRepository<Classes, Long> {

    List<Classes> findAllByValueLessThan(BigDecimal value);

    List<Classes> findByClassTypesId(Integer id);

    @Override
    List<Classes> findAll();


    List<Classes> findAllByOrderByValueDesc();


    List<Classes> findAllByOrderByValueAsc();

    @Override
    <S extends Classes> S save(S entity);

    //new
    List<Classes> findByCode(String code);

    List<Classes> findByClassTypesIdOrderByValueAsc(Integer id);

    List<Classes> findByInstructorId(Integer id);

    List<Classes> findAllByOrderByValue();

    List<Classes> findByLocationCity(@Param("city") String city);

    List<Classes> findAllByNameLike(@Param("name") String name);

    List<Classes> findAllByNameLikeOrderByValueDesc(@Param("name") String name);

    List<Classes> findAllByNameLikeOrderByValueAsc(@Param("name") String name);

    List<Classes> findAllByClassTypesId(Long id);

}


/// ADD "/search" ON THE URL REQUEST IF THE METHOD STARTS WITH "find"