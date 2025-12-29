package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.Billing;
import com.example.FabriqBackend.service.impl.BillingServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/billing")
@RequiredArgsConstructor
public class BillingController {

    private final BillingServiceImpl billingService;

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
}
