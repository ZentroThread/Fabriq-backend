package com.example.FabriqBackend.dto.salary;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ProductionRecordRequestDTO {
    private LocalDate date;
    private String productionName;;
    private Integer quantity;
    private Double ratePerProduct;
    private Long empId;
}
