package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.DeductionTypeDto;
import com.example.FabriqBackend.service.IDeductionTypeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/deduction-types")
@RequiredArgsConstructor
public class DeductionTypeController {

    private final IDeductionTypeService deductionTypeService;

    @PostMapping
    @Operation(
        summary = "Create Deduction Type",
        description = "Creates a new deduction type with the provided details."
    )
    public ResponseEntity<DeductionTypeDto> create(@RequestBody DeductionTypeDto deductionTypeDto) {
        return deductionTypeService.createDeductionType(deductionTypeDto);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update Deduction Type",
        description = "Updates the deduction type identified by the given ID with the provided details."
    )
    public ResponseEntity<DeductionTypeDto> update(@PathVariable Long id, @RequestBody DeductionTypeDto deductionTypeDto) {
        return deductionTypeService.updateDeductionType(id, deductionTypeDto);
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete Deduction Type",
        description = "Deletes the deduction type identified by the given ID."
    )
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return deductionTypeService.deleteDeductionType(id);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get Deduction Type by ID",
        description = "Retrieves the deduction type identified by the given ID."
    )
    public ResponseEntity<DeductionTypeDto> getById(@PathVariable Long id) {
        return deductionTypeService.getDeductionTypeById(id);
    }

    @GetMapping
    @Operation(
        summary = "Get All Deduction Types",
        description = "Retrieves a list of all deduction types."
    )
    public ResponseEntity<java.util.List<DeductionTypeDto>> getAll() {
        return deductionTypeService.getAllDeductionTypes();
    }
}
