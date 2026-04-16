package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.config.AppConfig;
import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dao.DeviceAttendanceLogDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.enums.AttendanceStatus;
import com.example.FabriqBackend.mapper.AttendanceMapper;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.DeviceAttendanceLog;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.service.IAttendanceService;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@AllArgsConstructor
@Slf4j

public class AttendanceServiceImpl implements IAttendanceService {

    private final AttendanceDao attendanceDao;
    private final EmployeeDao employeeDao;
    private final DeviceAttendanceLogDao deviceAttendanceLogDao;

    private static final LocalTime SHIFT_START = LocalTime.of(9, 0);
    private static final LocalTime SHIFT_END = LocalTime.of(17, 0);
    private static final int ALLOWED_LATE_MINUTES = 10;
    private static final int REQUIRED_HOURS = 8;

    public void markAttendance(AttendanceCreateDto dto) {
        log.info("markAttendance called for empId={}", dto != null ? dto.getEmpId() : null);
        if (dto == null || dto.getEmpId() == null || dto.getEmpId() <= 0) {
            throw new RuntimeException("empCode is required to mark attendance");
        }
        Employee employee = employeeDao.findById(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with Id: " + dto.getEmpId()));

        Attendance attendance = AttendanceMapper.toEntity(dto, new Attendance());
        attendance.setEmployee(employee);
        attendanceDao.save(attendance);
        log.info("Attendance recorded for empId={}", dto.getEmpId());
    }

    public List<AttendanceDto> getMonthlyAttendanceByEmpCode(String empCode, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendanceList = attendanceDao
                .findByEmployee_EmpCodeAndDateBetweenOrderByDateAsc(empCode, start, end);

        if (attendanceList.isEmpty()) {
            throw new RuntimeException("Attendance not found for empCode:" + empCode + " month: " + month);
        }

        return attendanceList.stream()
                .map(att -> AttendanceMapper.toDto(att, new AttendanceDto()))
                .toList();
    }

    public List<AttendanceDto> fetchAllAttendanceForMonth(int year, int month) {
        log.info("Fetching all attendance for year={} month={}", year, month);
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendanceList = attendanceDao.findByDateBetweenOrderByDateAsc(start, end);

        return attendanceList.stream()
                .map(att -> AttendanceMapper.toDto(att, new AttendanceDto()))
                .toList();
    }

    public List<AttendanceDto> fetchAllAttendanceForDate(String date) {
        log.info("Fetching attendance for date={}", date);
        LocalDate localDate = LocalDate.parse(date);

        List<Attendance> attendances = attendanceDao.findByDate(localDate);

        return attendances.stream()
            .map(att -> AttendanceMapper.toDto(att, new AttendanceDto()))
            .toList();
    }

    @Transactional
    public void updateDailyAttendance(String empCode, LocalDate date) {
        log.info("Updating daily attendance for empCode={} date={}", empCode, date);

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        List<DeviceAttendanceLog> logs =
                deviceAttendanceLogDao.findByEmpCodeAndPunchTimeBetween(empCode, start, end);

        Attendance attendance = attendanceDao
                .findByEmployee_EmpCodeAndDate(empCode, date)
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setEmployee(employeeDao.findByEmpCode(empCode)
                            .orElseThrow(() -> new RuntimeException("Employee not found")));
                    a.setDate(date);
                    return a;
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime shiftStartTime = date.atTime(SHIFT_START);
        LocalDateTime graceEndTime = shiftStartTime.plusMinutes(ALLOWED_LATE_MINUTES);

        if (logs.isEmpty()) {
            log.info("No device logs found for empCode={} on date={}. Marking as absent or pending.", empCode, date);
            attendance.setCheckIn(null);
            attendance.setCheckOut(null);
            attendance.setTotalHours(0.0);
            attendance.setLateMinutes(0L);

            if (date.isBefore(LocalDate.now())) {
                attendance.setStatus(AttendanceStatus.ABSENT);
            } else if (date.isEqual(LocalDate.now()) && now.isAfter(graceEndTime)) {
                attendance.setStatus(AttendanceStatus.ABSENT);
            } else {
                attendance.setStatus(null);
            }

            attendanceDao.save(attendance);
            log.debug("Attendance saved for empCode={} with status={}", empCode, attendance.getStatus());
            return;
        }

        LocalDateTime firstIn = logs.stream()
                .filter(l -> "IN".equalsIgnoreCase(l.getDirection()))
                .map(DeviceAttendanceLog::getPunchTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastOut = logs.stream()
                .filter(l -> "OUT".equalsIgnoreCase(l.getDirection()))
                .map(DeviceAttendanceLog::getPunchTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        attendance.setCheckIn(firstIn != null ? firstIn.toLocalTime() : null);
        attendance.setCheckOut(lastOut != null ? lastOut.toLocalTime() : null);
        log.debug("Computed checkIn={} checkOut={} for empCode={}", attendance.getCheckIn(), attendance.getCheckOut(), empCode);

        if (firstIn != null && lastOut == null) {
            log.info("Only check-in found for empCode={}; marking partial attendance.", empCode);
            attendance.setTotalHours(0.0);
            long lateMinutes = Duration.between(shiftStartTime, firstIn).toMinutes();
            attendance.setLateMinutes(lateMinutes > ALLOWED_LATE_MINUTES ? lateMinutes : 0L);
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendanceDao.save(attendance);
            log.debug("Saved attendance for empCode={} with lateMinutes={}", empCode, attendance.getLateMinutes());
            return;
        }

        if (firstIn != null && lastOut != null && lastOut.isAfter(firstIn)) {
            long workedMinutes = Duration.between(firstIn, lastOut).toMinutes();
            double totalHours = workedMinutes / 60.0;

            attendance.setTotalHours(totalHours);

            long lateMinutes = Duration.between(shiftStartTime, firstIn).toMinutes();
            attendance.setLateMinutes(lateMinutes > ALLOWED_LATE_MINUTES ? lateMinutes : 0L);

            if (workedMinutes < (REQUIRED_HOURS * 60) / 2) {
                attendance.setStatus(AttendanceStatus.HALF_DAY);
            } else if (workedMinutes < (REQUIRED_HOURS * 60)) {
                attendance.setStatus(AttendanceStatus.HALF_DAY);
            } else {
                attendance.setStatus(AttendanceStatus.PRESENT);
            }

            attendanceDao.save(attendance);
            log.info("Attendance updated for empCode={} date={} totalHours={} lateMinutes={} status={}", empCode, date, totalHours, attendance.getLateMinutes(), attendance.getStatus());
        }
    }
}
