package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;

import java.util.List;

public interface IAttendanceService {

     void markAttendance(AttendanceCreateDto dto);
     List<AttendanceDto> getMonthlyAttendanceByEmpCode(String empCode, int year, int month);
     List<AttendanceDto> fetchAllAttendanceForMonth(int year, int month);
     List<AttendanceDto> fetchAllAttendanceForDate(String date);

}
