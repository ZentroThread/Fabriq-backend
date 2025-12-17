package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.AllowanceTypeDTO;
import com.example.FabriqBackend.service.IAllowanceTypeService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/allowance-types")
@RequiredArgsConstructor
public class AllowanceTypeController {

    private final IAllowanceTypeService allowanceTypeService;

    @PostMapping
    @Operation(
        summary = "Create Allowance Type",
        description = "Creates a new allowance type with the provided details."
    )
    public ResponseEntity<AllowanceTypeDTO> create(@RequestBody AllowanceTypeDTO allowanceTypeDTO) {
        return allowanceTypeService.createAllowanceType(allowanceTypeDTO);
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update Allowance Type",
        description = "Updates the allowance type identified by the given ID with the provided details."
    )
    public ResponseEntity<AllowanceTypeDTO> update(@PathVariable Long id, @RequestBody AllowanceTypeDTO allowanceTypeDTO) {
        return allowanceTypeService.updateAllowanceType(id, allowanceTypeDTO);
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get Allowance Type by ID",
        description = "Retrieves the allowance type identified by the given ID."
    )
    public ResponseEntity<AllowanceTypeDTO> getById(@PathVariable Long id) {
        return allowanceTypeService.getAllowanceTypeById(id);
    }

    @GetMapping
    @Operation(
        summary = "Get All Allowance Types",
        description = "Retrieves a list of all allowance types."
    )
    public ResponseEntity<List<AllowanceTypeDTO>> getAllAllowanceTypes() {
        return allowanceTypeService.getAllAllowanceTypes();
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete Allowance Type",
        description = "Deletes the allowance type identified by the given ID."
    )
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return allowanceTypeService.deleteAllowanceType(id);
    }
}
