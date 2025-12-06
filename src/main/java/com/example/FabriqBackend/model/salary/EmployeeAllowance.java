package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_allowances")
@Setter @Getter
@AllArgsConstructor @NoArgsConstructor
public class EmployeeAllowance extends TenantAwareEntity {

    @Id
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "allowance_id")
    private AllowanceType allowanceType;
}
