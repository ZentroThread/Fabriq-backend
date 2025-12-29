package com.example.FabriqBackend.model.salary;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class CommissionSlab {
    private final Integer minAmount;
    private final Integer maxAmount;
    private final Integer commissionPercentage;

}
