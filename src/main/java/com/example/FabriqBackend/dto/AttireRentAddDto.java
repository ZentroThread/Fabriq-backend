package com.example.FabriqBackend.dto;

import lombok.Data;

import java.util.Date;

@Data
public class AttireRentAddDto {

    private Date rentDate;
    private Date returnDate;
    private String customerCode;
    private String attireCode;
    private String billingCode;
}
