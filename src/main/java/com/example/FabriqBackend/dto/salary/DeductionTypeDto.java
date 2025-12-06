package com.example.FabriqBackend.dto.salary;

import com.example.FabriqBackend.enums.DeductionTypeEnum;
import lombok.Data;

@Data
public class DeductionTypeDto {
    private String name;
    private Double amount;
    private DeductionTypeEnum type;
}
