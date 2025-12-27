package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.AdvancePaymentRequestDTO;
import com.example.FabriqBackend.dto.salary.AdvancePaymentResponseDTO;
import com.example.FabriqBackend.service.IAdvancePaymentService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RequiredArgsConstructor
@Controller
@RequestMapping("/v1/advance-payments")
public class AdvancePaymentController {

    private final IAdvancePaymentService advancePaymentService;

    @PostMapping
    @Operation(
            summary = "Create Advance Payment",
            description = "This endpoint allows creating a new advance payment record for an employee."
    )
    public ResponseEntity<AdvancePaymentResponseDTO> createAdvancePayment(@RequestBody AdvancePaymentRequestDTO requestDTO) {
        AdvancePaymentResponseDTO responseDTO = advancePaymentService.createAdvancePayment(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/employee/{empId}")
    @Operation(
            summary = "Get Advance Payments by Employee ID",
            description = "This endpoint retrieves all advance payment records for a specific employee by their ID."
    )
    public ResponseEntity<List<AdvancePaymentResponseDTO>> getAdvancePaymentsByEmployeeId(@PathVariable Long empId) {
        List<AdvancePaymentResponseDTO> responseDTO = advancePaymentService.getAdvancePaymentsByEmployeeId(empId);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping
    @Operation(
            summary = "Get All Advance Payments",
            description = "This endpoint retrieves all advance payment records."
    )
    public ResponseEntity<List<AdvancePaymentResponseDTO>> getAllAdvancePayments() {
        List<AdvancePaymentResponseDTO> responseDTO = advancePaymentService.getAllAdvancePayments();
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Advance Payment",
            description = "This endpoint allows deleting an advance payment record by its ID."
    )
    public ResponseEntity<?> deleteAdvancePayment(@PathVariable Long id) {
        advancePaymentService.deleteAdvancePayment(id);
        HashMap<String, String> response = new HashMap<>();
        response.put("deletedId", id.toString());
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Advance Payment",
            description = "This endpoint allows updating an existing advance payment record by its ID."
    )
    public ResponseEntity<AdvancePaymentResponseDTO> updateAdvancePayment(@PathVariable Long id, @RequestBody AdvancePaymentRequestDTO requestDTO) {
        AdvancePaymentResponseDTO responseDTO = advancePaymentService.updateAdvancePayment(id, requestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/employee/{empId}/date-range")
    @Operation(
            summary = "Get Advance Payments by Employee ID and Date Range",
            description = "This endpoint retrieves advance payment records for a specific employee within a specified date range."
    )
    public ResponseEntity<List<AdvancePaymentResponseDTO>> getAdvancePaymentsByEmployeeIdAndDateRange(
            @PathVariable Long empId,
            @RequestParam String startDate,
            @RequestParam String endDate) {
        List<AdvancePaymentResponseDTO> responseDTO = advancePaymentService.getAdvancePaymentsByEmployeeIdAndDateRange(empId, startDate, endDate);
        return ResponseEntity.ok(responseDTO);
    }
}
