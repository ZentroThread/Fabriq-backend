package com.example.FabriqBackend.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class BookingRequestDto {
    public Long tenantId;
    public Long attireId;
    public LocalDate startDate;
    public LocalDate endDate;
    public String userEmail;
}