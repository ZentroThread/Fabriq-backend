package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.model.salary.CommissionSlab;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
public class CommissionCalculationService {

    private static final List<CommissionSlab> slabs = List.of(
            new CommissionSlab(0, 1_000_000, 2),
            new CommissionSlab(1_000_000, 3_000_000, 3),
            new CommissionSlab(3_000_000, 6_000_000, 4)
    );

    public static double calculateCommission(double salesAmount) {
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


}
