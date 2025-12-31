package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "payroll_records")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PayrollRecord extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer month;
    private Integer year;

    private Double totalAllowances;
    private Double totalDeductions;
    private Double commission;
    private Double overtimePay;
    private Double salaryAdvance;
    private Double productPay;
    //private Double poyaDayBonus;

    private Double epfEmployeeContribution;
    private Double epfEmployerContribution;
    private Double etf;

    private Double grossSalary;
    private Double netSalary;

    private boolean confirmed = false;

    private LocalDateTime generatedAt;

    @ManyToOne
    @JoinColumn(name = "emp_id", nullable = false)
    private Employee employee;
}
