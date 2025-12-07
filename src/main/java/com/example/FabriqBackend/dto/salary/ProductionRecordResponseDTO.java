package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class ProductionRecordResponseDTO {
    private Long id;
    private String date;
    private String productionName;
    private Integer quantity;
    private Double ratePerProduct;
    private Long empId;
    private String empCode;
    private String empName;
}
