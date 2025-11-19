package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.dto.ResponseDto;
import com.example.FabriqBackend.service.AttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    //mark attendance http://localhost:8081/attendance
    @PostMapping
    public ResponseEntity<?> markAttendance(@RequestBody AttendanceDto dto){
        attendanceService.markAttendance(dto);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200","Attendance marked successfully"));
    }

    //get attendance for an employee for a month http://localhost:8081/attendance/EMP001/month?year=2025&month=1
    @GetMapping("/{empCode}/month")
    public ResponseEntity<List<AttendanceDto>> getMonthlyByEmp(
            @PathVariable String empCode,
            @RequestParam int year,
            @RequestParam int month) {

        List<AttendanceDto> monthlyAttendance = attendanceService.getMonthlyAttendanceByEmpCode(empCode, year, month);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(monthlyAttendance);
    }

    //get attendance for all employees for a month http://localhost:8081/attendance/month?year=2025&month=1
    @GetMapping("/month")
    public ResponseEntity<List<AttendanceDto>> getAllForMonth(
            @RequestParam int year,
            @RequestParam int month) {

        List<AttendanceDto> monthlyAll = attendanceService.fetchAllAttendanceForMonth(year, month);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(monthlyAll);
    }

    //get attendance for  employees for a date http://localhost:8081/attendance/EMP001/date?date=2025-11-01
    @GetMapping("/date")
    public ResponseEntity<List<AttendanceDto>> getAttendanceByDate(
            @RequestParam String date) {
        List<AttendanceDto> attendanceByDate = attendanceService.fetchAllAttendanceForDate(date);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(attendanceByDate);
    }

}
