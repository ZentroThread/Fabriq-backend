package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.DeductionType;
import org.springframework.stereotype.Repository;

@Repository
public interface DeductionTypeDao extends TenantAwareDao<DeductionType, Long> {
}
