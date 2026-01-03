package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.EmployeeAllowanceDao;
import com.example.FabriqBackend.enums.AllowanceTypeEnum;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AllowanceType;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AllowanceService {

    private final EmployeeAllowanceDao allowanceDao;

    public double calculate(Employee employee){

        return allowanceDao.findByEmployee_Id(employee.getId())
                .orElse(List.of())
                .stream()
                .mapToDouble(a ->{
                    AllowanceType type = a.getAllowanceType();
                    if (type == null) return 0.0;

                    double amount = Optional.ofNullable(type.getAmount()).orElse(0.0);

                    return type.getType() == AllowanceTypeEnum.FIXED
                            ? amount
                            : employee.getBasicSalary() * (amount/ 100.0);
                })
                .sum();
    }
}
