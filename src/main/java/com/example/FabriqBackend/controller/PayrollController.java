package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.service.impl.PayrollCalculationServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollCalculationServiceImpl payrollCalculationService;

    @GetMapping("/calculate/{empId}/{month}/{year}")
    public ResponseEntity<PayrollResponseDTO> calculatePayroll(@PathVariable  Long empId,@PathVariable Integer month,@PathVariable Integer year) {
        PayrollResponseDTO payroll = payrollCalculationService.calculatePayroll(empId, month, year);
        return ResponseEntity.ok(payroll);
    }

    @PostMapping("/confirm/{empId}/{month}/{year}")
    public ResponseEntity<PayrollResponseDTO> confirmPayroll(@PathVariable  Long empId,@PathVariable Integer month,@PathVariable Integer year) {
        PayrollResponseDTO payroll = payrollCalculationService.confirmAndSave(empId, month, year);
        return ResponseEntity.ok(payroll);
    }
}
