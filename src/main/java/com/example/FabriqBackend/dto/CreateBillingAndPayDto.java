package com.example.FabriqBackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class CreateBillingAndPayDto {
    private String customerCode;
    private List<AttireRentItemDto> items;
    private Double discountPercentage;
    private String paymentMethod;
}
