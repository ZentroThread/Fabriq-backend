package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class EmployeeAllowanceResponseDTO {
    private Long empId;
    private String empCode;
    private Long allowanceId;
    private String allowanceName;
    private String allowanceType;
    private Double allowanceAmount;
}
