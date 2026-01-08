package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.service.IEmployeeService;
import com.example.FabriqBackend.service.payroll.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.ui.Model;

import java.time.Month;
import java.util.Optional;

@Controller
@RequestMapping("/v1/payroll/payslip")
@RequiredArgsConstructor
public class PayslipPrintController {

    private final PayrollService payrollService;
    private final EmployeeDao employeeService;

    @GetMapping("/print/{tenantId}/{empId}/{month}/{year}")
    public String printPayslip(
            @PathVariable String tenantId,
            @PathVariable Long empId,
            @PathVariable int month,
            @PathVariable int year,
            Model model) {
        TenantContext.setCurrentTenant(tenantId);
        Optional<Employee> employeeOpt = employeeService.findById(empId);
        if (employeeOpt.isEmpty()) {
            throw new RuntimeException("Employee not found with ID " + empId);
        }
        Employee employee = employeeOpt.get();
        PayrollResponseDTO payroll = payrollService.calculate(empId, month, year);

        String monthName = Month.of(month).name();
        monthName = monthName.charAt(0) + monthName.substring(1).toLowerCase();

        model.addAttribute("monthName", monthName);
        model.addAttribute("e",employee);
        model.addAttribute("p",payroll);
        return "payslip";
    }

}
