package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeDao extends JpaRepository<Employee,Long> {
}
