package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.AttireRent;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttireRentDao extends TenantAwareDao<AttireRent, Integer> {

    AttireRent findByIdAndTenantId(Integer id, String tenantId);

    List<AttireRent> findAllByTenantId(String tenantId);


    List<AttireRent> findAllByBillingCode(String billingCode);
}
