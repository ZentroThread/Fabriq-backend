package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

public interface EmployeeDao extends JpaRepository<Employee,Long> {

    Optional<Employee>  findByEmpCode(String empCode);
    @Transactional
    @Modifying
    void deleteByEmpCode(String empCode);
}
