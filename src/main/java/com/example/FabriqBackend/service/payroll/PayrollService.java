package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.PayrollRecordDao;
import com.example.FabriqBackend.dto.salary.OvertimeResultDTO;
import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.mapper.PayrollMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.PayrollRecord;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class PayrollService {

    private final EmployeeDao employeeDao;
    private final PayrollRecordDao payrollRecordDao;

    private final AllowanceService allowanceService;
    private final DeductionService deductionService;
    private final ProductionPayService productionPayService;
    private final AdvancePaymentService advancePaymentService;
    private final OvertimeService overtimeService;
    private final CommissionService commissionService;
    private final StatutoryService statutoryService;
    private final HolidayService holidayService;

    public PayrollResponseDTO calculate(Long empId, int month, int year) {

        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee not found with Id: " + empId));

        YearMonth period = YearMonth.of(year, month);

        double basic = employee.getBasicSalary();

        double extraHolidays = holidayService.calculateExtraHolidaysTaken(employee, period);
        double holidayDeduction = holidayService.calculateExtraHolidayDeduction(employee, extraHolidays);

        double allowances = allowanceService.calculate(employee) - holidayDeduction;
        double deductions = deductionService.calculate(employee);
        double production = productionPayService.calculate(employee, period);
        double advance = advancePaymentService.calculate(employee, period);

        OvertimeResultDTO ot = overtimeService.calculate(employee, period);

        double commission = commissionService.calculate(employee,period);

        double epfEmp = statutoryService.epfEmployee(basic);
        double epfEmpr = statutoryService.epfEmployer(basic);
        double etf = statutoryService.etf(basic);

        double gross = basic + allowances + production + commission + ot.total();
        double net = gross - deductions - epfEmp - advance;

        return PayrollMapper.toDto(
                employee, month, year,
                basic, allowances, deductions,
                commission, advance, production,
                ot, epfEmp, epfEmpr, etf,
                gross, net , extraHolidays
        );
    }

    @Transactional
    public PayrollResponseDTO confirmAndSave(Long empId, int month, int year) {

        PayrollResponseDTO dto = calculate(empId, month, year);

        PayrollRecord record = PayrollRecord.builder()
                .employee(employeeDao.findById(empId).orElseThrow())
                .month(month)
                .year(year)

                .totalAllowances(dto.getTotalAllowances())
                .totalDeductions(dto.getTotalDeductions())
                .commission(dto.getCommission())
                .overtimePay(dto.getOvertimePay())
                .salaryAdvance(dto.getSalaryAdvance())
                .productPay(dto.getProductPay())

                .epfEmployeeContribution(dto.getEpfEmployee())
                .epfEmployerContribution(dto.getEpfEmployer())
                .etf(dto.getEtf())

                .grossSalary(dto.getGrossSalary())
                .netSalary(dto.getNetSalary())

                .confirmed(true)
                .generatedAt(dto.getCalculatedAt())
                .build();

        payrollRecordDao.save(record);

        return dto;
    }
}
