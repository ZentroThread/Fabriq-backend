package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.CreateBillingWithRentalsDto;
import com.example.FabriqBackend.dto.CreateBillingAndPayDto;
import com.example.FabriqBackend.model.Billing;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface IBillingService {
    ResponseEntity<?> addBilling(Billing billing);
    ResponseEntity<?> getAllBillings();
    ResponseEntity<?> createBillingWithRentals(CreateBillingWithRentalsDto dto);
    ResponseEntity<?> createBillingAndPay(CreateBillingAndPayDto dto);
    ResponseEntity<?> payBilling(com.example.FabriqBackend.dto.PayBillingDto dto);
    ResponseEntity<List<Billing>> getBillingByDateRange(LocalDateTime startDate, LocalDateTime endDate);
}
