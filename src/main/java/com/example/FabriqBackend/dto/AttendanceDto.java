package com.example.FabriqBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {
    private String empCode;
    private LocalDate date;
    private LocalTime time;
    private String status;  //IN or Out
}
