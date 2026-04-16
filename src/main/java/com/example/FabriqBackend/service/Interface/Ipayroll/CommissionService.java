package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.model.Employee;

import java.time.YearMonth;

public interface CommissionService {
    double calculateTotalCommission(YearMonth period);
    double calculate(Employee employee, YearMonth period);
}
