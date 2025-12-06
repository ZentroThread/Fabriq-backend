package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao extends TenantAwareDao<Employee,Long> {

    Optional<Employee>  findByEmpCode(String empCode);
    @Transactional
    @Modifying
    void deleteByEmpCode(String empCode);

    Optional<Employee> findByRole(String role);
}
