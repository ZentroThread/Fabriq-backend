package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.PayrollRecordDao;
import com.example.FabriqBackend.dto.salary.EtfFormDTO;
import com.example.FabriqBackend.model.Employee;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EtfReportService {

    private final PayrollRecordDao payrollRecordDao;

    public List<EtfFormDTO> getEtfForm(int month, int year) {

        return payrollRecordDao
                .findByMonthAndYearAndConfirmedTrue(month, year)
                .stream()
                .map(pr -> {

                    Employee e = pr.getEmployee();

                    double etfContribution = pr.getEtf();

                    return new EtfFormDTO(
                           e.getEmpFirstName()+" "+e.getEmpLastName(),
                           e.getNicNumber(),
                           e.getEpfNumber(),
                           e.getBasicSalary(),
                           etfContribution
                    );
                })
                .toList();
    }
}
