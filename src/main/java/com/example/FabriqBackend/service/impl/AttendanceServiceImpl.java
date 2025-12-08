package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.AppConfig;
import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.mapper.AttendanceMapper;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.service.IAttendanceService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService {

    private final AttendanceDao attendanceDao;
    private final AppConfig appConfig;
    private final EmployeeDao employeeDao;

    //mark attendance
    public void markAttendance(AttendanceCreateDto dto) {

        if (dto.getEmpCode() == null || dto.getEmpCode().isEmpty()) {
            throw new RuntimeException("empCode is required to mark attendance");
        }
        Employee employee = employeeDao.findByEmpCode(dto.getEmpCode())
                .orElseThrow(() -> new RuntimeException("Employee not found with empCode: " + dto.getEmpCode()));

        Attendance attendance = AttendanceMapper.toEntity(dto, new Attendance());
        attendance.setEmployee(employee);

        attendanceDao.save(attendance);
    }


    //get attendance for an employee for a month
    public List<AttendanceDto> getMonthlyAttendanceByEmpCode(String empCode, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendanceList = attendanceDao.findByEmpCodeAndDateBetweenOrderByDateAsc(empCode, start, end);
        if (attendanceList.isEmpty()) {
            throw new RuntimeException("Attendance not found for empCode:" + empCode + "for month: " + month + " and year: " + year);
        }
        return attendanceList
                .stream()
                .map(attendance -> AttendanceMapper.toDto(attendance,new AttendanceDto()))
                .toList();

    }

    //get attendance for all employees for a month
    public List<AttendanceDto> fetchAllAttendanceForMonth(int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());
        List<Attendance> attendanceList = attendanceDao.findByDateBetweenOrderByDateAsc(start, end);

        if (attendanceList.isEmpty()) {
            throw new RuntimeException("Attendance not found for month: " + month + " and year: " + year);
        }
        return attendanceList
                .stream()
                .map(attendance ->  AttendanceMapper.toDto(attendance,new AttendanceDto()))
                .toList();
    }

    //get attendance for all employees for a date
    public List<AttendanceDto> fetchAllAttendanceForDate(String date) {
        LocalDate localDate = LocalDate.parse(date);
        Optional<List<AttendanceDto>> attendanceList = attendanceDao.findByDate(localDate)
                .map(attendances -> attendances.stream()
                        .map(attendance ->  AttendanceMapper.toDto(attendance,new AttendanceDto()))
                        .toList());

        if (attendanceList.isEmpty()) {
            throw new RuntimeException("Attendance not found for date: " + date);
        } else {
            return attendanceList.get();
        }
    }
}
