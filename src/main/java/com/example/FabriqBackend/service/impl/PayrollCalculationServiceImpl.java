package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.*;
import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.enums.*;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;
import java.util.Objects;
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
    private final HolidayDao holidayDao;
    private final AdvancePaymentDao advancePaymentDao;

    // Constants
    private static final double EPF_EMPLOYEE_RATE = 0.08;
    private static final double EPF_EMPLOYER_RATE = 0.12;
    private static final double ETF_RATE = 0.03;
    private static final double SINGLE_OVERTIME_RATE = 1.5;
    private static final double DOUBLE_OVERTIME_RATE = 2.0;
    private static final int STANDARD_WORKING_HOURS = 160;
    private static final int STANDARD_WORKING_DAYS = 25;
    private static final int STANDARD_WORKING_HOURS_IN_DAY = 9;
    private static final int DEFAULT_MONTHLY_HOLIDAYS = 5;
    private static final double BREAK_TIME_HOURS = 1.0;

    // Calculate Payroll
    public PayrollResponseDTO calculatePayroll(Long empId, Integer month, Integer year) {

        var employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        double basicSalary = employee.getBasicSalary();

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();


        List<Holiday> holidays = holidayDao.findByDateBetween(startDate.toString(), endDate.toString());

        // Allowances
        double totalAllowances = getTotalAllowances(employee, basicSalary);

        // Deductions
        double totalDeductions = getTotalDeductions(employee, basicSalary);

        // Production Pay
        double productionPay = getProductionPay(employee, startDate, endDate);

        // Overtime Pay
        List<Attendance> attendances =
                attendanceDao.findByEmployee_IdAndDateBetween(employee.getId(), startDate, endDate)
                        .orElse(List.of());

        double doubleOTCHours = attendances.stream()
                .mapToDouble(a -> doubleOTCHoursCalculator(a.getTotalHours(), a.getDate(), holidays,a))
                .sum();
        double singleOTCHours = attendances.stream()
                .filter(a -> !isPoyaDay(a.getDate(), holidays))
                .mapToDouble(a -> singleOTCHoursCalculator(a.getTotalHours(),a))
                .sum();

        double doubleOTRate = ((basicSalary / STANDARD_WORKING_DAYS)/STANDARD_WORKING_HOURS_IN_DAY) * DOUBLE_OVERTIME_RATE;
        double singleOTRate = ((basicSalary / STANDARD_WORKING_DAYS)/STANDARD_WORKING_HOURS_IN_DAY) * SINGLE_OVERTIME_RATE;

        double doubleOTAmount = doubleOTCHours * doubleOTRate;
        double singleOTAmount = singleOTCHours * singleOTRate;

        double overtimePay = doubleOTAmount + singleOTAmount;

        int monthlyHolidays = DEFAULT_MONTHLY_HOLIDAYS;
        if(doubleOTAmount > 0){
            monthlyHolidays --;
        }

        // Extra Holidays Taken Deduction

        double absentDays = attendances.stream()
                .filter(a ->
                        !isPoyaDay(a.getDate(), holidays)
                )
                .mapToDouble(a -> a.getStatus().getDayValue())
                .sum();


        double extraHolidaysTaken = extraHolidaysTakenCalculator(
                absentDays,
                monthlyHolidays
        );

        double baseForDeduction =
                (totalAllowances > 0 ? totalAllowances : basicSalary);

        double attendanceAllowanceDeduction =
                (baseForDeduction / STANDARD_WORKING_DAYS) * extraHolidaysTaken;

        if(attendanceAllowanceDeduction > 0){
            totalAllowances -= attendanceAllowanceDeduction;
        }
        // Commission
        double salesEarned = 0.0;
        double totalCommissionAmount = CommissionCalculationService.calculateCommission(salesEarned);
        double commission =calculateCommission(
                employee,
                employeeDao.sumOfPerformancePointsOfCommissionEligibleEmployees(),
                totalCommissionAmount
        );

        // Salary Advance 
        double salaryAdvance = getTotalAdvancePayments(employee, startDate, endDate);

        // EPF/ETF
        double epfEmployee = basicSalary * EPF_EMPLOYEE_RATE;
        double epfEmployer = basicSalary * EPF_EMPLOYER_RATE;
        double etf = basicSalary * ETF_RATE;

        // Final salary calculations
        double gross = basicSalary + totalAllowances + commission + overtimePay + productionPay;
        double net = gross - totalDeductions - epfEmployee - salaryAdvance;

        return getPayrollResponseDTO(month, year, employee, basicSalary, totalAllowances, totalDeductions, commission, salaryAdvance, productionPay,
                singleOTCHours,singleOTAmount,doubleOTCHours,doubleOTAmount,singleOTRate,doubleOTRate, overtimePay, epfEmployee, epfEmployer, etf, gross, net,extraHolidaysTaken);
    }

    // Confirm and Save Payroll
    public PayrollResponseDTO confirmAndSave(Long empId, Integer month, Integer year) {

        PayrollResponseDTO calc = calculatePayroll(empId, month, year);

        extracted(empId, month, year, calc);

        return calc;
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

    //Advance Payment
    private double getTotalAdvancePayments(Employee employee, LocalDate startDate, LocalDate endDate) {
        List<AdvancePayment> advancePayments =
                advancePaymentDao.findByEmployeeIdAndDateBetween(employee.getId(), startDate, endDate);
        if (advancePayments.isEmpty()) return 0.0;
        return advancePayments.stream()
                .mapToDouble(AdvancePayment::getAmount)
                .sum();
    }


    // Response DTO
    private static PayrollResponseDTO getPayrollResponseDTO(Integer month, Integer year, Employee employee, double basicSalary, double totalAllowances,
                                                            double totalDeductions, double commission, double salaryAdvance, double productionPay,double singleOTCHours,
                                                            double singleOTAmount,double doubleOTCHours,double doubleOTAmount,double singleOTRate,double doubleOTRate,
                                                            double overtimePay, double epfEmployee, double epfEmployer, double etf, double gross, double net,double extraHolidaysTaken) {
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
        resp.setSingleOTHours((double) Math.round(singleOTCHours));
        resp.setSingleOTRate((double) Math.round(singleOTRate));
        resp.setSingleOTAmount((double) Math.round(singleOTAmount));
        resp.setDoubleOTHours((double) Math.round(doubleOTCHours));
        resp.setDoubleOTRate((double) Math.round(doubleOTRate));
        resp.setDoubleOTAmount((double) Math.round(doubleOTAmount));
        resp.setOvertimePay((double) Math.round(overtimePay));
        resp.setEpfEmployee((double) Math.round(epfEmployee));
        resp.setEpfEmployer((double) Math.round(epfEmployer));
        resp.setEtf((double) Math.round(etf));
        resp.setGrossSalary((double) Math.round(gross));
        resp.setNetSalary((double) Math.round(net));
        resp.setCalculatedAt(LocalDateTime.now());
        resp.setExtraHolidaysTaken(extraHolidaysTaken);
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
                .confirmed(true)
                .generatedAt(calc.getCalculatedAt())
                .build();

        payrollRecordDao.save(record);
    }

    private static boolean isPoyaDay(LocalDate date, List<Holiday> holidays) {
        return holidays.stream()
                .anyMatch(holiday ->
                        holiday.getDate().equals(date.toString()) && holiday.getCategory() == HolidayCategoryEnum.POYA_DAY);
    }

    private static Double singleOTCHoursCalculator(Double totalHours,Attendance attendance) {
       if (Objects.equals(attendance.getStatus().toString(), AttendanceStatus.PRESENT.toString())) {
           double consideredHours = totalHours - BREAK_TIME_HOURS;
           if (consideredHours <= STANDARD_WORKING_HOURS_IN_DAY) {
               return 0.0;
           } else if (consideredHours > STANDARD_WORKING_HOURS_IN_DAY && consideredHours <= 12) {
               return consideredHours - STANDARD_WORKING_HOURS_IN_DAY;
           } else {
               return 3.0;
           }
       }
         return 0.0;
    }

    private static Double doubleOTCHoursCalculator(Double totalHours, LocalDate date, List<Holiday> holidays,Attendance attendance) {
        if (isPoyaDay(date, holidays) && attendance.getStatus().toString().equals(AttendanceStatus.PRESENT.toString()))
            return totalHours;
        return 0.0;
    }

    private static Double extraHolidaysTakenCalculator(double absentCount, int standardHolidays) {
        double extraHolidays = absentCount - standardHolidays;
        return Math.max(0.0, extraHolidays);
    }

    private double calculateCommission(Employee employee, double totalPoints, double totalCommissionAmount) {
        if (employee.isCommissionEligible() && totalPoints > 0) {
           return  (employee.getPerformancePoints() / totalPoints)
                            * totalCommissionAmount;
        }
        return 0.0;
    }

}
