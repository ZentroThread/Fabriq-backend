package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.EmployeeBankDetails;
import org.springframework.stereotype.Repository;

@Repository
public interface EmployeeBankDetailsDao extends TenantAwareDao<EmployeeBankDetails, Long> {

}
