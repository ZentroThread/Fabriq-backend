package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class EmployeeDeductionResponseDTO {
    private Long empId;
    private String empCode;
    private Long deductionId;
    private String deductionName;
    private String deductionType;
    private Double deductionAmount;
}
