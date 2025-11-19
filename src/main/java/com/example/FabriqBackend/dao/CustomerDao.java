package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Customer;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerDao extends TenantAwareDao<Customer, Integer> {
    Optional<Customer> findByCustCode(String custCode);
}
