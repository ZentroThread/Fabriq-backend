package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.EmployeeDeductionRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionResponseDTO;
import com.example.FabriqBackend.service.impl.EmployeeDeductionServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/employee-deductions")
@RequiredArgsConstructor
public class EmployeeDeductionController {

    private final EmployeeDeductionServiceImpl employeeDeductionService;

    @Operation(
        summary = "Create Employee Deduction",
        description = "Creates a new deduction record for an employee."
    )
    @PostMapping
    public ResponseEntity<EmployeeDeductionRequestDTO> createEmployeeDeduction(@RequestBody EmployeeDeductionRequestDTO requestDTO) {

        EmployeeDeductionRequestDTO createdDeduction = employeeDeductionService.createEmployeeDeduction(requestDTO);
        return ResponseEntity.ok(createdDeduction);

    }

    @Operation(
        summary = "Get Employee Deductions by Employee ID",
        description = "Retrieves all deduction records for a specific employee by their ID."
    )
    @GetMapping("/employee/{empId}")
    public ResponseEntity<List<EmployeeDeductionResponseDTO>> getEmployeeDeductionsByEmpId(@PathVariable Long empId) {
        List<EmployeeDeductionResponseDTO> deductions = employeeDeductionService.getEmployeeDeductionsByEmpId(empId);
        return ResponseEntity.ok(deductions);
    }

    @GetMapping
    @Operation(
        summary = "Get All Employee Deductions",
        description = "Retrieves all employee deduction records."
    )
    public ResponseEntity<List<EmployeeDeductionResponseDTO>> getAllEmployeeDeductions() {
        List<EmployeeDeductionResponseDTO> deductions = employeeDeductionService.getAllEmployeeDeductions();
        return ResponseEntity.ok(deductions);
    }

    @Operation(
        summary = "Delete Employee Deduction",
        description = "Deletes a specific deduction record for an employee."
    )
    @DeleteMapping("/{deductionId}/employee/{empId}" )
    public ResponseEntity<Void> deleteEmployeeDeduction(@PathVariable Long deductionId, @PathVariable Long empId) {
        employeeDeductionService.deleteEmployeeDeduction(deductionId, empId);
        return ResponseEntity.noContent().build();
    }
}
