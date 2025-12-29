package com.example.FabriqBackend.dto.salary;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor @AllArgsConstructor
@Builder
public class PayrollResponseDTO {

    private Integer empId;
    private String empCode;
    private String employeeName;

    private Integer month;
    private Integer year;

    private Double basicSalary;

    private Double totalAllowances;
    private Double totalDeductions;
    private Double commission;

    private Double doubleOTRate;
    private Double doubleOTAmount;
    private Double doubleOTHours;

    private Double singleOTRate;
    private Double singleOTHours;
    private Double singleOTAmount;

    private Double overtimePay;
    private Double extraHolidaysTaken;

    private Double salaryAdvance;
    private Double productPay;

    private Double epfEmployee;
    private Double epfEmployer;
    private Double etf;

    private Double grossSalary;
    private Double netSalary;

    private LocalDateTime calculatedAt;

}
