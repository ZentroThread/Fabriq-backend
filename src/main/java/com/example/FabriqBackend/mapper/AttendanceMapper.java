package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.model.Attendance;

import java.time.Duration;

public class AttendanceMapper {

    public static AttendanceDto toDto(Attendance attendance,AttendanceDto dto) {

        dto.setEmpId(attendance.getEmployee() != null ? attendance.getEmployee().getId() : null);
        dto.setDate(attendance.getDate());
        dto.setCheckIn(attendance.getCheckIn());
        dto.setCheckOut(attendance.getCheckOut());
        dto.setTotalHours(attendance.getTotalHours());
        dto.setLateMinutes(attendance.getLateMinutes());
        dto.setStatus(attendance.getStatus());

        return dto;
    }

    public static Attendance toEntity(AttendanceCreateDto dto, Attendance attendance) {

        attendance.setDate(dto.getDate());
        attendance.setCheckIn(dto.getCheckIn());
        attendance.setCheckOut(dto.getCheckOut());
        attendance.setStatus(dto.getStatus());

        if(attendance.getCheckIn() != null && attendance.getCheckOut() != null) {
            long hours = Duration.between(attendance.getCheckIn(), attendance.getCheckOut()).toHours();
            attendance.setTotalHours((double) hours);
        } else {
            attendance.setTotalHours(0.0);
        }

        return attendance;
    }
}
