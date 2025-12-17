package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.DeductionTypeDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.EmployeeDeductionDao;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionResponseDTO;
import com.example.FabriqBackend.mapper.EmployeeDeductionMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.DeductionType;
import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import com.example.FabriqBackend.service.IEmployeeDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeDeductionServiceImpl implements IEmployeeDeductionService {

    private final EmployeeDeductionDao employeeDeductionDao;
    private final EmployeeDao employeeDao;
    private final DeductionTypeDao deductionTypeDao;

    // Create Employee Deduction
    public EmployeeDeductionRequestDTO createEmployeeDeduction(EmployeeDeductionRequestDTO requestDTO) {

        Employee employee = employeeDao.findById(requestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + requestDTO.getEmpId()));
        DeductionType deductionType = deductionTypeDao.findById(requestDTO.getDeductionId())
                .orElseThrow(() -> new RuntimeException("DeductionType not found with id: " + requestDTO.getDeductionId()));

        EmployeeDeduction saveEmployeeDeduction = new com.example.FabriqBackend.model.salary.EmployeeDeduction();
        saveEmployeeDeduction.setEmployee(employee);
        saveEmployeeDeduction.setDeductionType(deductionType);
        employeeDeductionDao.save(saveEmployeeDeduction);

        return requestDTO;
    }

    // Get Employee Deduction by Employee ID
    public List<EmployeeDeductionResponseDTO> getEmployeeDeductionsByEmpId(Long empId) {

       Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));

       Optional<List<EmployeeDeduction>> employeeDeductions = employeeDeductionDao.findByEmployee_Id(empId);

        return employeeDeductions.orElse(List.of()).stream()
                .map(EmployeeDeductionMapper::toDto)
                .toList();
    }

    // Get All Employee Deductions
    public List<EmployeeDeductionResponseDTO> getAllEmployeeDeductions() {
        List<EmployeeDeduction> employeeDeductions = employeeDeductionDao.findAll();
        return employeeDeductions.stream()
                .map(EmployeeDeductionMapper::toDto)
                .toList();
    }

    // Delete Employee Deduction
    public void deleteEmployeeDeduction(Long id,Long empId) {
        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
        EmployeeDeduction employeeDeduction = employeeDeductionDao.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeDeduction not found with id: " + id));
        employeeDeductionDao.deleteByIdAndEmployee_Id(id,empId);
    }
}
