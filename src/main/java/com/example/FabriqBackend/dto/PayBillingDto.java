package com.example.FabriqBackend.dto;

import lombok.Data;

@Data
public class PayBillingDto {
    private String billingCode;
    private Double discountPercentage; // 0-100
    private String paymentMethod; // cash|card
}
