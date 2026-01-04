package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.CreateBillingWithRentalsDto;
import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.service.IBillingService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final IBillingService billingService;

    @PostMapping("/add")
    @Operation(
            summary = "Add a new billing record",
            description = "Creates a new billing entry in the system with the provided billing details."
    )
    public ResponseEntity<?> addBilling(@RequestBody Billing billing) {
        return billingService.addBilling(billing);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all billing records",
            description = "Fetches a complete list of all billing records stored in the system."
    )
    public ResponseEntity<?> getAllBillings() {
        return billingService.getAllBillings();
    }
    @PostMapping("/create-with-rentals")
    @Operation(summary = "Create billing with rentals", description = "Create a billing entry and attach related rental records in a single request")
    public ResponseEntity<?> createBillingWithRentals(@RequestBody CreateBillingWithRentalsDto dto) {
        return billingService.createBillingWithRentals(dto);
    }

    @PostMapping("/pay")
    @Operation(summary = "Pay billing", description = "Process a payment for a billing record")
    public ResponseEntity<?> payBilling(@RequestBody com.example.FabriqBackend.dto.PayBillingDto dto) {
        return billingService.payBilling(dto);
    }
}
