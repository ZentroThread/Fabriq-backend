package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.PayrollRecordDao;
import com.example.FabriqBackend.dto.salary.EpfFormDTO;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.PayrollRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EpfReportService {

    private final PayrollRecordDao payrollRecordDao;

    public List<EpfFormDTO> getEpfForm(int month, int year) {

        return payrollRecordDao
                .findByMonthAndYearAndConfirmedTrue(month, year)
                .stream()
                .map(pr -> {

                    Employee e = pr.getEmployee();

                    double total = pr.getEpfEmployeeContribution()
                            + pr.getEpfEmployerContribution();

                    return new EpfFormDTO(
                            e.getEmpFirstName() + " " + e.getEmpLastName(),
                            e.getNicNumber(),
                            e.getEpfNumber(),
                            e.getBasicSalary(),
                            pr.getEpfEmployeeContribution(),
                            pr.getEpfEmployerContribution(),
                            total
                    );
                })
                .toList();
    }

}
