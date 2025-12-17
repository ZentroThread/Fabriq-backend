package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Table(name = "employee_bank_details")
public class EmployeeBankDetails extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountHolderName;

}
