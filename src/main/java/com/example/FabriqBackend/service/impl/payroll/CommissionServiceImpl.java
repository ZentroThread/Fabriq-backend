package com.example.FabriqBackend.service.impl.payroll;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.CommissionSlab;
import com.example.FabriqBackend.service.Interface.Ipayroll.CommissionService;
import com.example.FabriqBackend.service.Interface.IBillingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommissionServiceImpl implements CommissionService {

    private final EmployeeDao employeeDao;
    private final IBillingService billingService;

    private static final List<CommissionSlab> slabs = List.of(
            new CommissionSlab(0, 1_000_000, 2),
            new CommissionSlab(1_000_000, 3_000_000, 3),
            new CommissionSlab(3_000_000, 6_000_000, 4)
    );

    public  double calculateTotalCommission(YearMonth period) {

        LocalDateTime start = period.atDay(1).atStartOfDay();
        LocalDateTime end = period.atEndOfMonth().atTime(23,59,59);
        List<Billing> billings = billingService.getBillingByDateRange(start, end).getBody();
        assert billings != null;
        double salesAmount = billings.stream()
                .mapToDouble(b -> Double.parseDouble(b.getBillingTotal()))
                .sum();

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
