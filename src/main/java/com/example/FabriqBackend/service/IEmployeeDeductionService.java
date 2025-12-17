package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.EmployeeDeductionRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionResponseDTO;

import java.util.List;

public interface IEmployeeDeductionService {
     EmployeeDeductionRequestDTO createEmployeeDeduction(EmployeeDeductionRequestDTO requestDTO);
     List<EmployeeDeductionResponseDTO> getEmployeeDeductionsByEmpId(Long empId);
     List<EmployeeDeductionResponseDTO> getAllEmployeeDeductions();
     void deleteEmployeeDeduction(Long id,Long empId);
}
