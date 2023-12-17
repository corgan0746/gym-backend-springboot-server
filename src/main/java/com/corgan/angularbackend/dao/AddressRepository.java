package com.corgan.angularbackend.dao;

import com.corgan.angularbackend.entity.Address;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AddressRepository extends JpaRepository<Address, Long> {

    @Override
    void delete(Address entity);
}
