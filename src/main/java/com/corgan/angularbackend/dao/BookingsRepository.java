package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.Bookings;
import com.corgan.angularbackend.entity.Classes;
import com.corgan.angularbackend.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingsRepository extends JpaRepository<Bookings, Long> {
    void deleteById(int id);
    void deleteByCustomer(Customer customer);

    List<Bookings> findAllByCustomerAndClasses(Customer customer, Classes classes);

    @Override
    <S extends Bookings> S save(S entity);
}
