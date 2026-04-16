package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.service.IAttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/v1/attendance")
public class AttendanceController {
    private final IAttendanceService attendanceService;

    @PostMapping
    @Operation(
            summary = "Mark attendance for an employee",
            description = "This endpoint allows marking attendance for an employee by" +
                    " providing the necessary details in the request body."
    )
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceCreateDto dto) {
        attendanceService.markAttendance(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @GetMapping("/{empCode}/month")
    @Operation(
            summary = "Get monthly attendance for an employee",
            description = "This endpoint retrieves the attendance records for a specific employee for a given month and year." +
                    "http://localhost:8081/attendance/EMP001/month?year=2025&month=1"
    )
    public ResponseEntity<List<AttendanceDto>> getMonthlyByEmp(
            @PathVariable String empCode,
            @RequestParam int year,
            @RequestParam int month) {

        List<AttendanceDto> monthlyAttendance = attendanceService.getMonthlyAttendanceByEmpCode(empCode, year, month);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(monthlyAttendance);
    }

    @GetMapping("/month")
    @Operation(
            summary = "Get attendance for all employees for a specific month",
            description = "This endpoint retrieves the attendance records for all employees for a given month and year." +
                    "http://localhost:8081/attendance/month?year=2025&month=1 "
    )
    public ResponseEntity<List<AttendanceDto>> getAllForMonth(
            @RequestParam int year,
            @RequestParam int month) {

        List<AttendanceDto> monthlyAll = attendanceService.fetchAllAttendanceForMonth(year, month);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(monthlyAll);
    }

    @GetMapping("/date")
    @Operation(
            summary = "Get attendance for all employees on a specific date",
            description = "This endpoint retrieves the attendance records for all employees on a given date. " +
                    "http://localhost:8081/attendance/EMP001/date?date=2025-11-01"
    )
    public ResponseEntity<List<AttendanceDto>> getAttendanceByDate(
            @RequestParam String date) {
        List<AttendanceDto> attendanceByDate = attendanceService.fetchAllAttendanceForDate(date);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attendanceByDate);
    }

}
