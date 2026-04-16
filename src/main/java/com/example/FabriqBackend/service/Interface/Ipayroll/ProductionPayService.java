package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.model.Employee;

import java.time.YearMonth;

public interface ProductionPayService {
    double calculate(Employee employee, YearMonth period);
}
