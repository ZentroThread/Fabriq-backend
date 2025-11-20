package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.EmployeeDto;

import java.util.List;

public interface IEmployeeService {
    EmployeeDto addEmployee(EmployeeDto dto);
    EmployeeDto updateEmployee(EmployeeDto dto,String empCode);
    void deleteEmployee(String empCode);
    EmployeeDto fetchEmployeeById(String empCode);
    List<EmployeeDto> fetchAllEmployees();
}
