package com.example.FabriqBackend.model;

import com.example.FabriqBackend.enums.GenderEnum;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import com.example.FabriqBackend.model.salary.EmployeeBankDetails;
import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import com.example.FabriqBackend.model.salary.ProductionRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Entity
@Getter
@Setter
public class Employee extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long  id;

    @Column(nullable = false,unique = true)
    private String empCode;

    private String empFirstName;
    private String empLastName;
    private String nicNumber;
    private String mobileNumber;
    private String dateOfBirth;
    private String role;
    private String address;
    @Enumerated(EnumType.STRING)
    private GenderEnum gender;
    private String joinedDate;
    private String epfNumber;
    private Double basicSalary;

    //production records relationship
    @OneToMany (mappedBy = "employee", cascade = CascadeType.ALL)
    private List<ProductionRecord> productionRecords;

    //employee allowances relationship
    @OneToMany (mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeAllowance> employeeAllowances;

    //employee deductions relationship
    @OneToMany (mappedBy = "employee", cascade = CascadeType.ALL)
    private List<EmployeeDeduction> employeeDeductions;

    //attendance relationship
    @OneToMany(mappedBy = "employee", cascade = CascadeType.ALL)
    private List<Attendance> attendances;

    //employee bank details relationship
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bank_details_id")
    private EmployeeBankDetails employeeBankDetails;


}
