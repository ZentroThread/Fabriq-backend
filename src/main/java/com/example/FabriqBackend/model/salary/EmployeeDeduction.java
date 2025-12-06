package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.model.Employee;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "employee_deductions")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class EmployeeDeduction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;

    @ManyToOne
    @JoinColumn(name = "deduction_id", nullable = false)
    private DeductionType deductionType;
}
