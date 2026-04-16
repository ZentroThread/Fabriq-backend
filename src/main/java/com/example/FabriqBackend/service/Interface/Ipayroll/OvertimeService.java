package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.dto.salary.OvertimeResultDTO;
import com.example.FabriqBackend.model.Employee;

import java.time.YearMonth;

public interface OvertimeService {
    OvertimeResultDTO calculate(Employee employee, YearMonth period);
}
