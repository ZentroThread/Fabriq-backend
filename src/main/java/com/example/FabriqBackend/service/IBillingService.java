package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.CreateBillingWithRentalsDto;
import com.example.FabriqBackend.model.Billing;
import org.springframework.http.ResponseEntity;

public interface IBillingService {
    ResponseEntity<?> addBilling(Billing billing);
    ResponseEntity<?> getAllBillings();
    ResponseEntity<?> createBillingWithRentals(CreateBillingWithRentalsDto dto);
}
