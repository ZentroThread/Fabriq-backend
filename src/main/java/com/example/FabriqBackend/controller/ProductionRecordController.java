package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;
import com.example.FabriqBackend.service.impl.ProductionRecordServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/production-records")
@RequiredArgsConstructor
public class ProductionRecordController {

    private final ProductionRecordServiceImpl ProductionRecordServiceImpl;

    @PostMapping
    @Operation(
            summary = "Create Production Record",
            description = "Create a new production record for an employee."
    )
    public ResponseEntity<ProductionRecordRequestDTO> createRecord(@RequestBody ProductionRecordRequestDTO requestDTO) {

        ProductionRecordRequestDTO created = ProductionRecordServiceImpl.createProductionRecord(requestDTO);
        return ResponseEntity.ok(created);
    }

    @GetMapping
    @Operation(
            summary = "Get All Production Records",
            description = "Retrieve all production records."
    )
    public ResponseEntity<List<ProductionRecordResponseDTO>> getAll() {

        return ResponseEntity.ok(ProductionRecordServiceImpl.getAllProductionRecords());
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "Get Production Records by Date Range",
            description = "Retrieve production records within a specified date range."
    )
    public ResponseEntity<List<ProductionRecordResponseDTO>> getByDateRange(
            @RequestParam String startDate,
            @RequestParam String endDate) {

        return ResponseEntity.ok(
                ProductionRecordServiceImpl.getRecordsByDateRange(startDate, endDate)
        );
    }

    @GetMapping("/employee/{empId}")
    @Operation(
            summary = "Get Production Records by Employee",
            description = "Retrieve production records for a specific employee."
    )
    public ResponseEntity<List<ProductionRecordResponseDTO>> getByEmployee(
            @PathVariable Long empId) {

        return ResponseEntity.ok(
                ProductionRecordServiceImpl.getRecordsByEmployeeId(empId)
        );
    }

    @GetMapping("/employee/{empId}/date-range")
    @Operation(
            summary = "Get Production Records by Employee and Date Range",
            description = "Retrieve production records for a specific employee within a specified date range."
    )
    public ResponseEntity<List<ProductionRecordResponseDTO>> getByEmployeeAndDateRange(
            @PathVariable Long empId,
            @RequestParam String startDate,
            @RequestParam String endDate) {

        return ResponseEntity.ok(
                ProductionRecordServiceImpl.getRecordsByDataRangeAndEmpId(startDate, endDate, empId)
        );
    }

    @DeleteMapping("/{recordId}")
    @Operation(
            summary = "Delete Production Record by Employee and Record ID",
            description = "Delete a specific production record for a specific employee."
    )
    public ResponseEntity<?> deleteByEmployeeAndRecordId(
            @PathVariable Long recordId) {

        ProductionRecordServiceImpl.deleteProductionRecordById(recordId);
        Map<String, String> response = new HashMap<>();
        response.put("deletedRecord", recordId.toString() );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{recordId}")
    @Operation(
            summary = "Update Production Record",
            description = "Update a specific production record."
    )
    public ResponseEntity<ProductionRecordResponseDTO> updateRecord(
            @PathVariable Long recordId,
            @RequestBody ProductionRecordRequestDTO requestDTO) {
        ProductionRecordResponseDTO updated = ProductionRecordServiceImpl.updateProductionRecord(recordId, requestDTO);
        return ResponseEntity.ok(updated);
    }

}
