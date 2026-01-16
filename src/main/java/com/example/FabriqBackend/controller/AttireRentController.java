package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttireRentAddDto;
import com.example.FabriqBackend.dto.AttireRentDto;
import com.example.FabriqBackend.service.IAttireRentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/attire-rent")// Base URL for attire rent-related operations
@RequiredArgsConstructor
public class AttireRentController {

    private final IAttireRentService attireRentService;

    @PostMapping("/add")
    @Operation(
            summary = "Create a new attire rent record",
            description = "Adds a new attire rental entry including customer details, rental dates, deposit amount, and associated attire items."
    )
    public ResponseEntity<?> addAttireRent(@RequestBody AttireRentAddDto dto) {
        return attireRentService.addAttireRent(dto);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all attire rent records",
            description = "Fetches a complete list of all attire rental records stored in the system."
    )
    public ResponseEntity<List<AttireRentDto>> getAllAttireRent() {
        return ResponseEntity.ok(attireRentService.getAllAttireRent());
    }

    @GetMapping("/by-billing/{code}")
    @Operation(summary = "Get rents by billing code", description = "Fetch rentals for a specific billing code (tenant-scoped)")
    public ResponseEntity<?> getByBillingCode(@PathVariable String code) {
        return attireRentService.getAttireRentsByBillingCode(code);
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete attire rent by ID",
            description = "Deletes an existing attire rental record identified by its ID. Returns an error if the record does not exist."
    )
    public ResponseEntity<?> deleteAttireRent(@PathVariable Integer id) {
        return attireRentService.deleteAttireRent(id);
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update an existing attire rent record",
            description = "Updates the details of an existing attire rental entry using the provided data. Only fields passed in the DTO will be updated."
    )
    public ResponseEntity<?> updateAttireRent(@PathVariable Integer id, @RequestBody AttireRentAddDto dto) {
        return attireRentService.updateAttireRent(id, dto);
    }
}
