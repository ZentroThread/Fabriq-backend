package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeDeductionDao extends TenantAwareDao<EmployeeDeduction, Long> {

    Optional<List<EmployeeDeduction>> findByEmployee_Id(Long employeeId);

    @Modifying
    void deleteByIdAndEmployee_Id(Long id, Long employeeId);
}
