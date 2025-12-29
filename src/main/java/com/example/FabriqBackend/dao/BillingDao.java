package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Billing;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillingDao extends TenantAwareDao<Billing, Integer> {

    List<Billing> findAllByTenantId(String tenatId);
}
