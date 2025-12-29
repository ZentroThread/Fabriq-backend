package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class HolidayRequestDTO {
    private String date;
    private String description;
    private String category;
    private String payType;
}
