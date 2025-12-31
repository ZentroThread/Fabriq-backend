package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class HolidayResponseDTO {
    private int id;
    private String date;
    private String description;
    private String category;
    private String payType;
}
