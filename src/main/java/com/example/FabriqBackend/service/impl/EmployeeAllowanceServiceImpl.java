package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AllowanceTypeDao;
import com.example.FabriqBackend.dao.EmployeeAllowanceDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceResponseDTO;
import com.example.FabriqBackend.mapper.EmployeeAllowanceMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AllowanceType;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import com.example.FabriqBackend.service.IEmployeeAllowanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeAllowanceServiceImpl implements IEmployeeAllowanceService {

    private final EmployeeDao employeeDao;
    private final AllowanceTypeDao allowanceTypeDao;
    private final EmployeeAllowanceDao employeeAllowanceDao;

    // Create Employee Allowance
    public EmployeeAllowanceRequestDTO createEmployeeAllowance(EmployeeAllowanceRequestDTO requestDTO) {

        Employee employee = employeeDao.findById(requestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + requestDTO.getEmpId()));
        AllowanceType allowanceType = allowanceTypeDao.findById(requestDTO.getAllowanceId())
                .orElseThrow(() -> new RuntimeException("AllowanceType not found with id: " + requestDTO.getAllowanceId()));

        EmployeeAllowance saveEmployeeAllowance = new EmployeeAllowance();
        saveEmployeeAllowance.setEmployee(employee);
        saveEmployeeAllowance.setAllowanceType(allowanceType);

        employeeAllowanceDao.save(saveEmployeeAllowance);

        return requestDTO;
    }

    // Get Employee Allowances by Employee ID
    public List<EmployeeAllowanceResponseDTO> getEmployeeAllowancesByEmpId(Long empId) {

        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));

        Optional<List<EmployeeAllowance>> employeeAllowances = employeeAllowanceDao.findByEmployee_Id(empId);

        return employeeAllowances.orElse(List.of()).stream()
                .map(EmployeeAllowanceMapper::toDto)
                .toList();
    }

    // Get All Employee Allowances
    public List<EmployeeAllowanceResponseDTO> getAllEmployeeAllowances() {

        List<EmployeeAllowance> employeeAllowances = employeeAllowanceDao.findAll();

        return employeeAllowances.stream()
                .map(EmployeeAllowanceMapper::toDto)
                .toList();

    }

    // Delete Employee Allowance
    public void deleteEmployeeAllowance(Long id,Long empId) {
        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + empId));
        EmployeeAllowance employeeAllowance = employeeAllowanceDao.findById(id)
                .orElseThrow(() -> new RuntimeException("EmployeeAllowance not found with id: " + id));
        employeeAllowanceDao.deleteByIdAndEmployee_Id(id, empId);
    }

}
