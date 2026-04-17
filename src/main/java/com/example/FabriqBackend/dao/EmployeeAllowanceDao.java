package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeAllowanceDao extends TenantAwareDao<EmployeeAllowance, Long> {

    Optional<List<EmployeeAllowance>> findByEmployee_Id(Long employeeId);

    @Transactional
    @Modifying
    void deleteByIdAndEmployee_Id(Long id, Long employeeId);
}
