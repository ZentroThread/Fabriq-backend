package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.AllowanceType;
import org.springframework.stereotype.Repository;

@Repository
public interface AllowanceTypeDao extends TenantAwareDao<AllowanceType, Long> {
}
