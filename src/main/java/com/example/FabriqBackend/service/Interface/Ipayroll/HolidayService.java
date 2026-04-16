package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.model.Employee;

import java.time.YearMonth;

public interface HolidayService {
    double calculateExtraHolidaysTaken(Employee employee, YearMonth period);
    double calculateExtraHolidayDeduction(Employee employee,double extraHolidays);
}
