package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dao.HolidayDao;
import com.example.FabriqBackend.dto.salary.OvertimeResultDTO;
import com.example.FabriqBackend.enums.AttendanceStatus;
import com.example.FabriqBackend.enums.HolidayCategoryEnum;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.Holiday;
import com.example.FabriqBackend.util.PayrollConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OvertimeService {

    private final AttendanceDao attendanceDao;
    private final HolidayDao holidayDao;

    public OvertimeResultDTO calculate(Employee employee, YearMonth period) {

        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        List<Holiday> holidays =
                holidayDao.findByDateBetween(start.toString(), end.toString());

        List<Attendance> attendances =
                attendanceDao.findByEmployee_IdAndDateBetween(employee.getId(), start, end)
                        .orElse(List.of());

        double singleHours = 0;
        double doubleHours = 0;

        for (Attendance a : attendances) {

            if (a.getStatus() != AttendanceStatus.PRESENT) continue;

            if (isPoyaDay(a.getDate(), holidays)) {
                doubleHours += a.getTotalHours();
                continue;
            }

            double worked = a.getTotalHours() - PayrollConstants.BREAK_TIME_HOURS;

            if (worked > PayrollConstants.STANDARD_WORKING_HOURS_IN_DAY) {
                singleHours += Math.min(
                        worked - PayrollConstants.STANDARD_WORKING_HOURS_IN_DAY,
                        3
                );
            }
        }

        double hourlyRate =
                (employee.getBasicSalary() / PayrollConstants.STANDARD_WORKING_DAYS)
                        / (PayrollConstants.STANDARD_WORKING_HOURS_IN_DAY - PayrollConstants.BREAK_TIME_HOURS);

        double singleOTHourlyRate = hourlyRate * PayrollConstants.SINGLE_OVERTIME_RATE;
        double doubleOTHourlyRate = hourlyRate * PayrollConstants.DOUBLE_OVERTIME_RATE;

        double singleAmount = singleHours > 0
                ? singleHours * singleOTHourlyRate
                : 0.0;
        double doubleAmount = doubleHours > 0
                ? doubleHours * doubleOTHourlyRate
                : 0.0;




        return new OvertimeResultDTO(
                singleHours,
                doubleHours,
                singleAmount,
                doubleAmount,
                singleOTHourlyRate,
                doubleOTHourlyRate
        );
    }

    private boolean isPoyaDay(LocalDate date, List<Holiday> holidays) {
        return holidays.stream()
                .anyMatch(h ->
                        h.getCategory() == HolidayCategoryEnum.POYA_DAY &&
                                h.getDate().equals(date.toString()));
    }
}


