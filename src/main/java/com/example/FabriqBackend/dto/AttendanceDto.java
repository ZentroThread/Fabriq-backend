package com.example.FabriqBackend.dto;

import com.example.FabriqBackend.enums.AttendanceStatus;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceDto {

    private Long empId;
    private LocalDate date;
    private LocalTime checkIn;
    private LocalTime checkOut;
    private Double totalHours;
    private Long lateMinutes;
    private AttendanceStatus status;

}
