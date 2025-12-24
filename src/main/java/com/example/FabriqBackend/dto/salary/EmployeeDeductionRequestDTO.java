package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class EmployeeDeductionRequestDTO {
    private Long empId;
    private Long deductionId;
}
