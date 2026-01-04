package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.service.IPayrollService;
import com.example.FabriqBackend.service.impl.PayrollCalculationServiceImpl;
import com.example.FabriqBackend.service.payroll.PayrollService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService payrollCalculationService;
    private final IPayrollService payrollService;

    @GetMapping("/calculate/{empId}/{month}/{year}")
    @Operation(summary = "Calculate payroll for an employee", description = "Calculate payroll for a given employee for a specific month and year")
    public ResponseEntity<PayrollResponseDTO> calculatePayroll(@PathVariable  Long empId,@PathVariable Integer month,@PathVariable Integer year) {
        PayrollResponseDTO payroll = payrollCalculationService.calculate(empId, month, year);
        return ResponseEntity.ok(payroll);
    }

    @PostMapping("/confirm/{empId}/{month}/{year}")
    @Operation(summary = "Confirm and save payroll", description = "Confirm calculated payroll and save the result for the given employee and month/year")
    public ResponseEntity<PayrollResponseDTO> confirmPayroll(@PathVariable  Long empId,@PathVariable Integer month,@PathVariable Integer year) {
        PayrollResponseDTO payroll = payrollCalculationService.confirmAndSave(empId, month, year);
        return ResponseEntity.ok(payroll);
    }

    @GetMapping("/{empId}/{year}")
    @Operation(summary = "Get payroll records by employee ID and year", description = "Retrieve all payroll records for a specific employee in a given year")
    public ResponseEntity<?> getPayrollRecordsByEmployeeIdAndYear(@PathVariable Long empId, @PathVariable Integer year) {
        return ResponseEntity.ok(payrollService.getPayrollRecordsByEmployeeIdAndYear(empId, year));
    }

    @GetMapping("/epf-record/{month}/{year}")
    @Operation(summary = "Get EPF records for a specific month and year", description = "Retrieve EPF form data for all employees for a given month and year")
    public ResponseEntity<?> getEpfRecords(@PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payrollService.getEpfFormData(month, year));
    }

    @GetMapping("/etf-record/{month}/{year}")
    @Operation(summary = "Get ETF records for a specific month and year", description = "Retrieve ETF form data for all employees for a given month and year")
    public ResponseEntity<?> getEtfRecords(@PathVariable Integer month, @PathVariable Integer year) {
        return ResponseEntity.ok(payrollService.getEtfFormData(month, year));
    }
}
