package com.corgan.angularbackend.customdao;

import com.corgan.angularbackend.entity.Customer;

import java.util.List;

public interface CustomerDAO {
    List<Customer> getFullCustomerDetails(int id);

    boolean checkEmail(String email);

    Customer getCustomerByEmail(String email);

    boolean saveCustomer(Customer customer);

}
