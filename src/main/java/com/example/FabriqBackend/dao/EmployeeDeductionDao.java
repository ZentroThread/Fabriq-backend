package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface EmployeeDeductionDao extends TenantAwareDao<EmployeeDeduction, Long> {

    Optional<List<EmployeeDeduction>> findByEmployee_Id(Long employeeId);

    @Transactional
    @Modifying
    void deleteByIdAndEmployee_Id(Long id, Long employeeId);
}
