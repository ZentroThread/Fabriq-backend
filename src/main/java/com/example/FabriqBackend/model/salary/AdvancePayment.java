package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "advance_payments")
public class AdvancePayment extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double amount;
    private String reason;
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "emp_id")
    private Employee employee;
}
