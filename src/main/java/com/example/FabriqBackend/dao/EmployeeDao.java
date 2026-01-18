package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Employee;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface EmployeeDao extends TenantAwareDao<Employee,Long> {

    Optional<Employee>  findByEmpCode(String empCode);
    @Transactional
    @Modifying
    void deleteByEmpCode(String empCode);

    Optional<Employee> findByRole(String role);

    @Query("SELECT DISTINCT e.tenantId FROM Employee e")
    List<String> findAllTenantIds();

    // private Integer performancePoints;
    @Query("""
        SELECT COALESCE(SUM(e.performancePoints), 0)
        FROM Employee e
        WHERE e.commissionEligible = true
    """)
    int sumOfPerformancePointsOfCommissionEligibleEmployees();


}
