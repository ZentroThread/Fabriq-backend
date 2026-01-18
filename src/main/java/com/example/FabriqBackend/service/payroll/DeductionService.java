package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.EmployeeDeductionDao;
import com.example.FabriqBackend.enums.DeductionTypeEnum;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.DeductionType;
import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DeductionService {

    private final EmployeeDeductionDao deductionDao;

    public double calculate(Employee employee){

        return deductionDao.findByEmployee_Id(employee.getId())
                .orElse(List.of())
                .stream()
                .mapToDouble(d ->{
                    DeductionType type = d.getDeductionType();
                    if (type == null) return 0.0;

                    double amount = Optional.ofNullable(type.getAmount()).orElse(0.0);

                    return type.getType() == DeductionTypeEnum.FIXED
                            ? amount
                            : employee.getBasicSalary() * (amount / 100.0);
                })
                .sum();
    }
}

