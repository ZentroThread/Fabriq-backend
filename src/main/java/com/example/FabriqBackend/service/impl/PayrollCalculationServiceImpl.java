package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.*;
import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.enums.AllowanceTypeEnum;
import com.example.FabriqBackend.enums.DeductionTypeEnum;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PayrollCalculationServiceImpl {

    // DAOs
    private final PayrollRecordDao payrollRecordDao;
    private final EmployeeDao employeeDao;
    private final AttendanceDao attendanceDao;
    private final EmployeeDeductionDao employeeDeductionDao;
    private final EmployeeAllowanceDao employeeAllowanceDao;
    private final ProductionRecordDao productionRecordDao;

    // Constants
    private static final double EPF_EMPLOYEE_RATE = 0.08;
    private static final double EPF_EMPLOYER_RATE = 0.12;
    private static final double ETF_RATE = 0.03;
    private static final double OVERTIME_RATE = 1.5;
    private static  final int STANDARD_WORKING_HOURS = 160;

    // Calculate Payroll
    public PayrollResponseDTO calculatePayroll(Long empId, Integer month, Integer year) {

        var employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        double basicSalary = employee.getBasicSalary();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        // Allowances
        double totalAllowances = getTotalAllowances(employee, basicSalary);

        // Deductions
        double totalDeductions = getTotalDeductions(employee, basicSalary);

        // Production Pay
        double productionPay = getProductionPay(employee, startDate, endDate);

        // Overtime Pay
        double overtimePay = getOvertimePay(employee, startDate, endDate, basicSalary);

        // Commission
        double commission = 0.0;

        // Salary Advance 
        double salaryAdvance = 0.0;

        // EPF/ETF
        double epfEmployee = basicSalary * EPF_EMPLOYEE_RATE;
        double epfEmployer = basicSalary * EPF_EMPLOYER_RATE;
        double etf = basicSalary * ETF_RATE;

        // Final salary calculations
        double gross = basicSalary + totalAllowances + commission + overtimePay + productionPay;
        double net = gross - totalDeductions - epfEmployee - salaryAdvance;

        return getPayrollResponseDTO(month, year, employee, basicSalary, totalAllowances, totalDeductions, commission, salaryAdvance, productionPay, overtimePay, epfEmployee, epfEmployer, etf, gross, net);
    }

    // Confirm and Save Payroll
    public PayrollResponseDTO confirmAndSave(Long empId, Integer month, Integer year) {

        PayrollResponseDTO calc = calculatePayroll(empId, month, year);

        extracted(empId, month, year, calc);

        return calc;
    }


    // Overtime Pay
    private  double getOvertimePay(Employee employee, LocalDate startDate, LocalDate endDate, double basicSalary) {

        List<Attendance> attendances =
                attendanceDao.findByEmployee_IdAndDateBetween(employee.getId(), startDate, endDate)
                        .orElse(List.of());
        double totalHoursWorked = attendances.stream()
                .mapToDouble(a -> Optional.ofNullable(a.getTotalHours()).orElse(0.0))
                .sum();
        double overtimeHours = Math.max(0.0, totalHoursWorked - STANDARD_WORKING_HOURS);
        return overtimeHours * (basicSalary / STANDARD_WORKING_HOURS) * OVERTIME_RATE;
    }
    // Production Pay
    private double getProductionPay(Employee employee, LocalDate startDate, LocalDate endDate) {

        List<ProductionRecord> productionRecords =
                productionRecordDao.findByEmployee_IdAndDateBetween(employee.getId(), startDate, endDate)
                        .orElse(List.of());

        return productionRecords.isEmpty()
            ? 0.0
            : productionRecords.stream()
                .mapToDouble(pr -> pr.getQuantity() * pr.getRatePerProduct())
                .sum();
    }

    // Deductions
    private double getTotalDeductions(Employee employee, double basicSalary) {

        List<EmployeeDeduction> deductions =
                employeeDeductionDao.findByEmployee_Id(employee.getId()).orElse(List.of());

        return deductions.stream().mapToDouble(ed -> {
            DeductionType dt = ed.getDeductionType();
            if (dt == null) return 0.0;

            double amount = Optional.ofNullable(dt.getAmount()).orElse(0.0);

            return dt.getType() == DeductionTypeEnum.FIXED
                    ? amount
                    : basicSalary * (amount / 100.0);
        }).sum();
    }

    // Allowances
    private double getTotalAllowances(Employee employee, double basicSalary) {

        List<EmployeeAllowance> allowances =
                employeeAllowanceDao.findByEmployee_Id(employee.getId()).orElse(List.of());

        return allowances.stream().mapToDouble(ea -> {
            AllowanceType at = ea.getAllowanceType();
            if (at == null) return 0.0;

            double amount = Optional.ofNullable(at.getAmount()).orElse(0.0);

            return at.getType() == AllowanceTypeEnum.FIXED
                    ? amount
                    : basicSalary * (amount / 100.0);
        }).sum();
    }


    // Response DTO
    private static PayrollResponseDTO getPayrollResponseDTO(Integer month, Integer year, Employee employee, double basicSalary, double totalAllowances, double totalDeductions, double commission, double salaryAdvance, double productionPay, double overtimePay, double epfEmployee, double epfEmployer, double etf, double gross, double net) {
        // Response DTO
        PayrollResponseDTO resp = new PayrollResponseDTO();
        resp.setEmpId(Math.toIntExact(employee.getId()));
        resp.setEmpCode(employee.getEmpCode());
        resp.setEmployeeName(employee.getEmpFirstName() + " " + employee.getEmpLastName());
        resp.setMonth(month);
        resp.setYear(year);
        resp.setBasicSalary(basicSalary);
        resp.setTotalAllowances((double) Math.round(totalAllowances));
        resp.setTotalDeductions((double) Math.round(totalDeductions));
        resp.setCommission((double) Math.round(commission));
        resp.setSalaryAdvance((double) Math.round(salaryAdvance));
        resp.setProductPay((double) Math.round(productionPay));
        resp.setOvertimePay((double) Math.round(overtimePay));
        resp.setEpfEmployee((double) Math.round(epfEmployee));
        resp.setEpfEmployer((double) Math.round(epfEmployer));
        resp.setEtf((double) Math.round(etf));
        resp.setGrossSalary((double) Math.round(gross));
        resp.setNetSalary((double) Math.round(net));
        resp.setCalculatedAt(LocalDateTime.now());
        return resp;
    }

    // Save Payroll Record
    private void extracted(Long empId, Integer month, Integer year, PayrollResponseDTO calc) {
        PayrollRecord record = PayrollRecord.builder()

                .employee(employeeDao.findById(empId).orElse(null))
                .month(month)
                .year(year)

                .totalAllowances(calc.getTotalAllowances())
                .totalDeductions(calc.getTotalDeductions())
                .commission(calc.getCommission())
                .overtimePay(calc.getOvertimePay())
                .salaryAdvance(calc.getSalaryAdvance())
                .productPay(calc.getProductPay())

                .epfEmployeeContribution(calc.getEpfEmployee())
                .epfEmployerContribution(calc.getEpfEmployer())
                .etf(calc.getEtf())

                .grossSalary(calc.getGrossSalary())
                .netSalary(calc.getNetSalary())

                .generatedAt(calc.getCalculatedAt())
                .build();

        payrollRecordDao.save(record);
    }
}
