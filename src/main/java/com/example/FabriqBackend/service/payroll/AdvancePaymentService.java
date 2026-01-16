package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.AdvancePaymentDao;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AdvancePayment;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class AdvancePaymentService {

    private final AdvancePaymentDao advancePaymentDao;

    public double calculate(Employee employee, YearMonth period) {

        return advancePaymentDao
                .findByEmployeeIdAndDateBetween(
                        employee.getId(),
                        period.atDay(1),
                        period.atEndOfMonth()
                )
                .stream()
                .mapToDouble(AdvancePayment::getAmount)
                .sum();
    }
}

