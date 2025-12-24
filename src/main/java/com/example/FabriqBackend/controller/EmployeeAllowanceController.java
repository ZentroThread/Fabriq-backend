package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.EmployeeAllowanceRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceResponseDTO;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import com.example.FabriqBackend.service.impl.EmployeeAllowanceServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/employee-allowances")
@RequiredArgsConstructor
public class EmployeeAllowanceController {

    private final EmployeeAllowanceServiceImpl employeeAllowanceService;

    @Operation(
            summary = "Add Employee Allowance",
            description = "Create a new allowance for an employee"
    )
    @PostMapping
    public ResponseEntity<EmployeeAllowanceRequestDTO> addEmployeeAllowance(@RequestBody EmployeeAllowanceRequestDTO employeeAllowanceDto) {
        employeeAllowanceService.createEmployeeAllowance(employeeAllowanceDto);
        return ResponseEntity.ok(employeeAllowanceDto);
    }

    @Operation(
            summary = "Get Employee Allowances by Employee ID",
            description = "Retrieve all allowances assigned to a specific employee"
    )
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<EmployeeAllowanceResponseDTO>> getByEmployee(@PathVariable Long empId) {
        List<EmployeeAllowanceResponseDTO> allowances = employeeAllowanceService.getEmployeeAllowancesByEmpId(empId);
        return ResponseEntity.ok(allowances);
    }

    @Operation(
            summary = "Get All Employee Allowances",
            description = "Retrieve all employee allowances in the system"
    )
    @GetMapping
    public ResponseEntity<List<EmployeeAllowanceResponseDTO>> getAllEmployeeAllowances() {
        List<EmployeeAllowanceResponseDTO> allowances = employeeAllowanceService.getAllEmployeeAllowances();
        return ResponseEntity.ok(allowances);
    }


    @Operation(
            summary = "Delete Employee Allowance",
            description = "Remove a specific allowance from an employee"
    )
    @DeleteMapping("/{id}/employee/{empId}")
    public ResponseEntity<Void> deleteEmployeeAllowance(@PathVariable Long id, @PathVariable Long empId) {
        employeeAllowanceService.deleteEmployeeAllowance(id, empId);
        return ResponseEntity.noContent().build();
    }

}
