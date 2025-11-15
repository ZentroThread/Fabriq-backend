package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.model.Login;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDao extends TenantAwareDao<Customer, Integer>{

    //Optional - help to safe handle null values
    Optional<Customer> findByCustCode(String custCode);
}
