package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dao.HolidayDao;
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
public class HolidayService {

    private final AttendanceDao attendanceDao;
    private final HolidayDao holidayDao;


    private static int standardHolidays = PayrollConstants.DEFAULT_MONTHLY_HOLIDAYS;

    public  double calculateExtraHolidaysTaken(Employee employee, YearMonth period) {

        LocalDate start = period.atDay(1);
        LocalDate end = period.atEndOfMonth();

        List<Attendance> attendance = attendanceDao
                .findByEmployee_IdAndDateBetween(employee.getId(), start, end)
                .orElse(null);

        double absentDays = attendance.stream()
                .filter(a ->
                        !isPoyaDay(a.getDate(), holidayDao.findByDateBetween(start.toString(), end.toString()))
                )
                .mapToDouble(a -> a.getStatus().getDayValue())
                .sum();

        boolean hasPoyaDayWork = attendance.stream()
                .anyMatch(a -> isPoyaDayWork(a, holidayDao.findByDateBetween(start.toString(), end.toString())));


        if(hasPoyaDayWork) {
            standardHolidays -= 1;
        }

        double extraHolidays = absentDays - standardHolidays;

        return Math.max(0.0, extraHolidays);

    }

    public double calculateExtraHolidayDeduction(Employee employee,double extraHolidays) {
        double dailyRate = employee.getBasicSalary() / PayrollConstants.STANDARD_WORKING_DAYS;
        return extraHolidays * dailyRate;
    }

    private static boolean isPoyaDay(LocalDate date, List<Holiday> holidays) {
        return holidays.stream()
                .anyMatch(holiday ->
                        holiday.getDate().equals(date.toString()) && holiday.getCategory() == HolidayCategoryEnum.POYA_DAY);
    }

    private static boolean isPoyaDayWork(Attendance attendance, List<Holiday> holidays) {
        return attendance.getStatus().equals(AttendanceStatus.PRESENT) && isPoyaDay(attendance.getDate(), holidays);
    }
}
