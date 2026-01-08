package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.EmployeeDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IEmployeeService {
    EmployeeDto addEmployee(EmployeeDto dto, MultipartFile image);
    EmployeeDto updateEmployee(EmployeeDto dto,String empCode, MultipartFile image);
    void deleteEmployee(String empCode);
    EmployeeDto fetchEmployeeById(String empCode);
    List<EmployeeDto> fetchAllEmployees();
    List<EmployeeDto> fetchEmployeeByRole(String role);
}
