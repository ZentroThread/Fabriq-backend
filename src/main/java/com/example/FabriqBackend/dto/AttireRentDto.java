package com.example.FabriqBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttireRentDto {
    private Integer id;
    private String attireCode;
    private String custCode;
    private String billingCode;
    private Integer rentDuration;
    private String rentDate; // ISO string
    private String returnDate; // ISO string
}
