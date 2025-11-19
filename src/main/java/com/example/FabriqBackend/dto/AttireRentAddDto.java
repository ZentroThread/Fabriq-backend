package com.example.FabriqBackend.dto;

import jakarta.persistence.Column;
import lombok.Data;

import java.util.Date;

@Data
public class AttireRentAddDto {

    private Date rentDate;
    private Date returnDate;
    private String customerCode;
    @Column(name = "attire_code", unique = false)
    private String attireCode;
}
