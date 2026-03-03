package com.example.FabriqBackend.service.impl;

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
public class AttendanceServiceImpl implements IAttendanceService {

    private final AttendanceDao attendanceDao;
    private final AppConfig appConfig;
    private final EmployeeDao employeeDao;
    private final DeviceAttendanceLogDao deviceAttendanceLogDao;

    // Attendance policy constants
    private static final LocalTime SHIFT_START = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime SHIFT_END = LocalTime.of(17, 0);  // 5:00 PM
    private static final int ALLOWED_LATE_MINUTES = 10;
    private static final int REQUIRED_HOURS = 8;

    // MARK ATTENDANCE MANUALLY
    public void markAttendance(AttendanceCreateDto dto) {
        if (dto.getEmpId() == null || dto.getEmpId() <= 0) {
            throw new RuntimeException("empCode is required to mark attendance");
        }
        Employee employee = employeeDao.findById(dto.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with Id: " + dto.getEmpId()));

        Attendance attendance = AttendanceMapper.toEntity(dto, new Attendance());
        attendance.setEmployee(employee);
        attendanceDao.save(attendance);
    }

    // FETCH MONTHLY ATTENDANCE BY EMPLOYEE
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

    // FETCH MONTHLY ATTENDANCE FOR ALL
    public List<AttendanceDto> fetchAllAttendanceForMonth(int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendanceList = attendanceDao.findByDateBetweenOrderByDateAsc(start, end);

        return attendanceList.stream()
                .map(att -> AttendanceMapper.toDto(att, new AttendanceDto()))
                .toList();
    }

    // FETCH DAILY ATTENDANCE
    public List<AttendanceDto> fetchAllAttendanceForDate(String date) {
        LocalDate localDate = LocalDate.parse(date);

        List<Attendance> attendances = attendanceDao.findByDate(localDate);

        return attendances.stream()
                .map(att -> AttendanceMapper.toDto(att, new AttendanceDto()))
                .toList();
    }

    // UPDATE DAILY ATTENDANCE BASED ON DEVICE LOGS
    @Transactional
    public void updateDailyAttendance(String empCode, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        // FETCH DEVICE LOGS FOR THE DAY
        List<DeviceAttendanceLog> logs =
                deviceAttendanceLogDao.findByEmpCodeAndPunchTimeBetween(empCode, start, end);

        // GET OR CREATE ATTENDANCE RECORD
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

        // CASE 1: NO LOGS
        if (logs.isEmpty()) {
            attendance.setCheckIn(null);
            attendance.setCheckOut(null);
            attendance.setTotalHours(0.0);
            attendance.setLateMinutes(0L);

            if (date.isBefore(LocalDate.now())) {
                attendance.setStatus(AttendanceStatus.ABSENT); // past date, no IN → ABSENT
            } else if (date.isEqual(LocalDate.now()) && now.isAfter(graceEndTime)) {
                attendance.setStatus(AttendanceStatus.ABSENT); // today after grace → ABSENT
            } else {
                attendance.setStatus(null); // before shift start → no status yet
            }

            attendanceDao.save(attendance);
            return;
        }

        // CASE 2: LOGS EXIST → FIND FIRST IN / LAST OUT
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

        // CASE 3: ONLY IN (no OUT yet)
        if (firstIn != null && lastOut == null) {
            attendance.setTotalHours(0.0);
            long lateMinutes = Duration.between(shiftStartTime, firstIn).toMinutes();
            attendance.setLateMinutes(lateMinutes > ALLOWED_LATE_MINUTES ? lateMinutes : 0L);
            attendance.setStatus(AttendanceStatus.PRESENT);
            attendanceDao.save(attendance);
            return;
        }

        // CASE 4: IN + OUT EXISTS
        if (firstIn != null && lastOut != null && lastOut.isAfter(firstIn)) {
            long workedMinutes = Duration.between(firstIn, lastOut).toMinutes();
            double totalHours = workedMinutes / 60.0;

            attendance.setTotalHours(totalHours);

            long lateMinutes = Duration.between(shiftStartTime, firstIn).toMinutes();
            attendance.setLateMinutes(lateMinutes > ALLOWED_LATE_MINUTES ? lateMinutes : 0L);

            // STATUS DECISION
            if (workedMinutes < (REQUIRED_HOURS * 60) / 2) {
                attendance.setStatus(AttendanceStatus.HALF_DAY);
            } else if (workedMinutes < (REQUIRED_HOURS * 60)) {
                attendance.setStatus(AttendanceStatus.HALF_DAY);
            } else {
                attendance.setStatus(AttendanceStatus.PRESENT);
            }

            attendanceDao.save(attendance);
        }
    }
}
