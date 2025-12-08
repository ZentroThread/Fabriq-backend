package com.example.FabriqBackend.dto;

import com.example.FabriqBackend.enums.AttendanceStatus;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AttendanceCreateDto {

    private Long empId;
    private String empCode;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private AttendanceStatus status;

}
