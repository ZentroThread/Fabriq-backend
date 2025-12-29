package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class PayrollRecordResponseDTO {
    private Integer month;
    private Integer year;
    private Double netSalary;
    private boolean confirmed;
}
