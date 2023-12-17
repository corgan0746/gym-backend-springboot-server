package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.Timeslots;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimeslotsRepository extends JpaRepository<Timeslots, Long> {
}
