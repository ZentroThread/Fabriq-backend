package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.OvertimeResultDTO;
import com.example.FabriqBackend.dto.salary.PayrollRecordResponseDTO;
import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.PayrollRecord;

import java.time.LocalDateTime;

public class PayrollMapper {

    public static PayrollRecordResponseDTO toRecordResponseDTO(PayrollRecord payrollRecord) {
        PayrollRecordResponseDTO dto = new PayrollRecordResponseDTO();
        dto.setMonth(payrollRecord.getMonth());
        dto.setYear(payrollRecord.getYear());
        dto.setNetSalary(payrollRecord.getNetSalary());
        dto.setConfirmed(payrollRecord.isConfirmed());
        return dto;
    }

    public static PayrollResponseDTO toDto(
            Employee emp,
            int month,
            int year,
            double basic,
            double allowances,
            double deductions,
            double commission,
            double advance,
            double production,
            OvertimeResultDTO ot,
            double epfEmp,
            double epfEmpr,
            double etf,
            double gross,
            double net,
            double extraHolidaysTaken
    ) {
        PayrollResponseDTO dto = new PayrollResponseDTO();

        dto.setEmpId(emp.getId().intValue());
        dto.setEmpCode(emp.getEmpCode());
        dto.setEmployeeName(emp.getEmpFirstName() + " " + emp.getEmpLastName());
        dto.setMonth(month);
        dto.setYear(year);

        dto.setBasicSalary(basic);
        dto.setTotalAllowances((double) Math.round(allowances));
        dto.setTotalDeductions((double) Math.round(deductions));
        dto.setCommission((double) Math.round(commission));
        dto.setSalaryAdvance((double) Math.round(advance));
        dto.setProductPay((double) Math.round(production));

        dto.setSingleOTHours(ot.getSingleHours());
        dto.setDoubleOTHours(ot.getDoubleHours());
        dto.setSingleOTRate(ot.getSingleOTHourlyRate());
        dto.setDoubleOTRate(ot.getDoubleOTHourlyRate());
        dto.setDoubleOTAmount(ot.getDoubleAmount());
        dto.setSingleOTAmount(ot.getSingleAmount());
        dto.setOvertimePay((double) Math.round(ot.total()));

        dto.setEpfEmployee((double) Math.round(epfEmp));
        dto.setEpfEmployer((double) Math.round(epfEmpr));
        dto.setEtf((double) Math.round(etf));

        dto.setGrossSalary((double) Math.round(gross));
        dto.setNetSalary((double) Math.round(net));
        dto.setCalculatedAt(LocalDateTime.now());

        dto.setExtraHolidaysTaken(extraHolidaysTaken);

        return dto;
    }
}
