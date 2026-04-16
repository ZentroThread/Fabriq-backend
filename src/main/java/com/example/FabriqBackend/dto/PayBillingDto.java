package com.example.FabriqBackend.dto;

import lombok.Data;

@Data
public class PayBillingDto {
    private String billingCode;
    private Double discountPercentage;
    private String paymentMethod;
}
