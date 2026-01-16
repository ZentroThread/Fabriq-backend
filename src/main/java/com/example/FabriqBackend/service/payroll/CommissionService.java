package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.CommissionSlab;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionService {

    private final EmployeeDao employeeDao;

    private static final List<CommissionSlab> slabs = List.of(
            new CommissionSlab(0, 1_000_000, 2),
            new CommissionSlab(1_000_000, 3_000_000, 3),
            new CommissionSlab(3_000_000, 6_000_000, 4)
    );

    public  double calculateTotalCommission(YearMonth period) {
         double salesAmount = 0.0;
        double totalCommission = 0.0;
        double remainingAmount = salesAmount;

        for (CommissionSlab slab : slabs) {
            if (remainingAmount <= 0) {
                break;
            }

            double slabRange = slab.getMaxAmount() - slab.getMinAmount();
            double applicableAmount = Math.min(remainingAmount, slabRange);

            totalCommission += (applicableAmount * slab.getCommissionPercentage()) / 100.0;
            remainingAmount -= applicableAmount;
        }

        return totalCommission;
    }

    public double calculate(Employee employee, YearMonth period) {

        double totalPoints = employeeDao.sumOfPerformancePointsOfCommissionEligibleEmployees();

        double totalCommissionAmount = calculateTotalCommission(period);

        if (employee.isCommissionEligible() && totalPoints > 0) {
            return  (employee.getPerformancePoints() / totalPoints)
                    * totalCommissionAmount;
        }
        return 0.0;
    }
}
