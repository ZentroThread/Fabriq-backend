package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.AppConfig;
import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dao.DeviceAttendanceLogDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.enums.AttendanceStatus;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.DeviceAttendanceLog;
import com.example.FabriqBackend.model.Employee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AttendanceServiceImplTest {

    @Mock
    private AttendanceDao attendanceDao;

    @Mock
    private EmployeeDao employeeDao;

    @Mock
    private DeviceAttendanceLogDao deviceAttendanceLogDao;

    @Mock
    private AppConfig appConfig;

    @InjectMocks
    private AttendanceServiceImpl attendanceService;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setEmpCode("EMP001");
    }

    // ---------------- MARK ATTENDANCE ----------------

    @Test
    void shouldMarkAttendanceSuccessfully() {
        AttendanceCreateDto dto = new AttendanceCreateDto();
        dto.setEmpId(1L);

        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));

        attendanceService.markAttendance(dto);

        verify(attendanceDao).save(any(Attendance.class));
    }

    @Test
    void shouldThrowExceptionWhenEmpIdIsNull() {
        AttendanceCreateDto dto = new AttendanceCreateDto();

        assertThrows(
                RuntimeException.class,
                () -> attendanceService.markAttendance(dto)
        );
    }

    // ---------------- MONTHLY ATTENDANCE ----------------

    @Test
    void shouldReturnMonthlyAttendance() {
        Attendance attendance = new Attendance();
        attendance.setDate(LocalDate.now());

        when(attendanceDao.findByEmployee_EmpCodeAndDateBetweenOrderByDateAsc(
                anyString(), any(), any()))
                .thenReturn(List.of(attendance));

        List<AttendanceDto> result =
                attendanceService.getMonthlyAttendanceByEmpCode("EMP001", 2024, 12);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowExceptionWhenMonthlyAttendanceNotFound() {
        when(attendanceDao.findByEmployee_EmpCodeAndDateBetweenOrderByDateAsc(
                anyString(), any(), any()))
                .thenReturn(List.of());

        assertThrows(
                RuntimeException.class,
                () -> attendanceService.getMonthlyAttendanceByEmpCode("EMP001", 2024, 12)
        );
    }

    // ---------------- FETCH ALL BY MONTH ----------------

    @Test
    void shouldFetchAllAttendanceForMonth() {
        Attendance attendance = new Attendance();
        attendance.setDate(LocalDate.now());

        when(attendanceDao.findByDateBetweenOrderByDateAsc(any(), any()))
                .thenReturn(List.of(attendance));

        List<AttendanceDto> result =
                attendanceService.fetchAllAttendanceForMonth(2024, 12);

        assertEquals(1, result.size());
    }

    // ---------------- FETCH BY DATE ----------------

    @Test
    void shouldFetchAttendanceByDate() {
        Attendance attendance = new Attendance();
        attendance.setDate(LocalDate.now());

        when(attendanceDao.findByDate(any()))
                .thenReturn(Optional.of(List.of(attendance)));

        List<AttendanceDto> result =
                attendanceService.fetchAllAttendanceForDate("2024-12-01");

        assertEquals(1, result.size());
    }

    // ---------------- UPDATE DAILY ATTENDANCE ----------------

    @Test
    void shouldUpdateDailyAttendanceSuccessfully() {
        LocalDate date = LocalDate.now();

        DeviceAttendanceLog inLog = new DeviceAttendanceLog();
        inLog.setDirection("IN");
        inLog.setPunchTime(LocalDateTime.of(date, LocalTime.of(9, 0)));

        DeviceAttendanceLog outLog = new DeviceAttendanceLog();
        outLog.setDirection("OUT");
        outLog.setPunchTime(LocalDateTime.of(date, LocalTime.of(17, 0)));

        when(deviceAttendanceLogDao.findByEmpCodeAndPunchTimeBetween(
                anyString(), any(), any()))
                .thenReturn(List.of(inLog, outLog));

        when(attendanceDao.findByEmployee_EmpCodeAndDate(anyString(), any()))
                .thenReturn(Optional.of(new Attendance()));

        attendanceService.updateDailyAttendance("EMP001", date);

        verify(attendanceDao).save(any(Attendance.class));
    }

    @Test
    void shouldDoNothingWhenNoLogsFound() {
        when(deviceAttendanceLogDao.findByEmpCodeAndPunchTimeBetween(
                anyString(), any(), any()))
                .thenReturn(List.of());

        attendanceService.updateDailyAttendance("EMP001", LocalDate.now());

        verify(attendanceDao, never()).save(any());
    }
}
