package com.example.FabriqBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class StockUpdate {
    private String attireCode;
    private Integer attireStock;
    private String reservedBy;
}
