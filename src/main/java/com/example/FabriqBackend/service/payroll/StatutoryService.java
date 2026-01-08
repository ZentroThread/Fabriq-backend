package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.AdvancePaymentDao;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AdvancePayment;
import com.example.FabriqBackend.util.PayrollConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;

@Service
@RequiredArgsConstructor
public class StatutoryService {

    private final AdvancePaymentDao advancePaymentDao;

    public double epfEmployee(double basicSalary){
        return basicSalary * PayrollConstants.EPF_EMPLOYEE_RATE;
    }

    public double epfEmployer(double basicSalary){
        return basicSalary * PayrollConstants.EPF_EMPLOYER_RATE;
    }

    public double etf(double basicSalary){
        return basicSalary * PayrollConstants.ETF_RATE;
    }
}
