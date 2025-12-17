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
import java.util.Optional;

@Service
@AllArgsConstructor
public class AttendanceServiceImpl implements IAttendanceService {

    private final AttendanceDao attendanceDao;
    private final AppConfig appConfig;
    private final EmployeeDao employeeDao;
    private final DeviceAttendanceLogDao deviceAttendanceLogDao;

    //attendance policy constants
    private static final LocalTime SHIFT_START = LocalTime.of(9, 0); // 9:00 AM
    private static final LocalTime SHIFT_END = LocalTime.of(17, 0); // 5:00 PM
    private static final int ALLOWED_LATE_MINUTES = 10;
    private static final int REQUIRED_HOURS = 8;

    //mark attendance
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


    //get attendance for an employee for a month
    public List<AttendanceDto> getMonthlyAttendanceByEmpCode(String empCode, int year, int month) {

        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        List<Attendance> attendanceList = attendanceDao.findByEmployee_EmpCodeAndDateBetweenOrderByDateAsc(empCode, start, end);
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

    @Transactional
    public void updateDailyAttendance(String empCode, LocalDate date) {

        LocalDateTime start = date.atStartOfDay();
        LocalDateTime end = date.atTime(23, 59, 59);

        // Fetch logs for the whole date
        List<DeviceAttendanceLog> logs =
                deviceAttendanceLogDao.findByEmpCodeAndPunchTimeBetween(empCode, start, end);

        if (logs.isEmpty()) {
            System.out.println("No logs found for empCode: " + empCode + " on date: " + date);
            return;
        };

        System.out.println("Logs for " + empCode + ": " + logs.size());

        Attendance attendance = attendanceDao.findByEmployee_EmpCodeAndDate(empCode, date)
                .orElseGet(() -> {
                    Attendance a = new Attendance();
                    a.setEmployee(employeeDao.findByEmpCode(empCode)
                            .orElseThrow(() -> new RuntimeException("Employee not found")));
                    a.setDate(date);
                    return a;
                });

        // Extract firstIn and lastOut
        LocalDateTime firstIn = logs.stream()
                .filter(l -> l.getDirection().equals("IN"))
                .map(DeviceAttendanceLog::getPunchTime)
                .min(LocalDateTime::compareTo)
                .orElse(null);

        LocalDateTime lastOut = logs.stream()
                .filter(l -> l.getDirection().equals("OUT"))
                .map(DeviceAttendanceLog::getPunchTime)
                .max(LocalDateTime::compareTo)
                .orElse(null);

        // Set checkIn / checkOut always
        attendance.setCheckIn(firstIn != null ? firstIn.toLocalTime() : null);
        attendance.setCheckOut(lastOut != null ? lastOut.toLocalTime() : null);

        // Worked minutes
        long workedMinutes = firstIn != null && lastOut != null
                ? Duration.between(firstIn, lastOut).toMinutes()
                : 0;

        // Late minutes
        long lateMinutes = firstIn != null
                ? Duration.between(date.atTime(SHIFT_START), firstIn).toMinutes()
                : 0;
        // Status
        if (firstIn == null && lastOut == null)
            attendance.setStatus(AttendanceStatus.ABSENT);
        else if (lateMinutes > ALLOWED_LATE_MINUTES)
            attendance.setStatus(AttendanceStatus.LATE);
        else if (workedMinutes < (REQUIRED_HOURS * 30))
            attendance.setStatus(AttendanceStatus.HALF_DAY);
        else if (workedMinutes < (REQUIRED_HOURS * 60))
            attendance.setStatus(AttendanceStatus.EARLY_LEAVE);
        else
            attendance.setStatus(AttendanceStatus.PRESENT);

        long totalHours = workedMinutes / 60;
        attendance.setTotalHours((double) totalHours);

        attendance.setLateMinutes(lateMinutes > ALLOWED_LATE_MINUTES ? lateMinutes : 0);

        attendanceDao.save(attendance);
    }
}
