package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class AdvancePaymentResponseDTO {
    private Long id;
    private Double amount;
    private String reason;
    private String date;
    private Long empId;
}
