package com.corgan.angularbackend.customdao;

import com.corgan.angularbackend.entity.Customer;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class CustomerDAOImpl implements CustomerDAO {

    private EntityManager entityManager;

    public CustomerDAOImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @Override
    public List<Customer> getFullCustomerDetails(int id) {
        TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c JOIN FETCH c.userMembership m JOIN FETCH c.bookings b WHERE c.id = :value", Customer.class);
        query.setParameter("value", id);
        List<Customer> list = query.getResultList();

        return list;
    }

    @Override
    public boolean checkEmail(String email) {
        TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.email = :value", Customer.class);
        query.setParameter("value", email);
        List<Customer> list = query.getResultList();

        return list.size() > 0;
    }

    @Override
    public Customer getCustomerByEmail(String email) {
        TypedQuery<Customer> query = entityManager.createQuery("SELECT c FROM Customer c WHERE c.email = :value", Customer.class);
        query.setParameter("value", email);
        List<Customer> list = query.getResultList();

        return list.get(0);
    }

    @Override
    @Transactional
    public boolean saveCustomer(Customer customer) {
        entityManager.persist(customer);
        return true;
    }


}
