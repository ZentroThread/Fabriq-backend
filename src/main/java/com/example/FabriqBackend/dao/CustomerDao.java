package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.model.Login;

import java.util.Optional;

public interface CustomerDao extends TenantAwareDao<Customer, Integer>{

    //Optional - help to safe handle null values
    Optional<Customer> findByCustCode(String custCode);
}
