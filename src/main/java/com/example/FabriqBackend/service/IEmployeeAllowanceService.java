package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.EmployeeAllowanceRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceResponseDTO;

import java.util.List;

public interface IEmployeeAllowanceService {
     EmployeeAllowanceRequestDTO createEmployeeAllowance(EmployeeAllowanceRequestDTO requestDTO);
     List<EmployeeAllowanceResponseDTO> getEmployeeAllowancesByEmpId(Long empId);
     List<EmployeeAllowanceResponseDTO> getAllEmployeeAllowances();
     void deleteEmployeeAllowance(Long id,Long empId);
}
