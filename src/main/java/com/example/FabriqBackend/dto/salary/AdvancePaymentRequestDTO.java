package com.example.FabriqBackend.dto.salary;

import lombok.Data;

import java.time.LocalDate;

@Data
public class AdvancePaymentRequestDTO {
    private Double amount;
    private String reason;
    private LocalDate date;
    private Long empId;
}
