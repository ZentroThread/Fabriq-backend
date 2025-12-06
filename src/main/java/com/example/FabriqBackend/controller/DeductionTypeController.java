package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.DeductionTypeDto;
import com.example.FabriqBackend.service.IDeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/deduction-types")
@RequiredArgsConstructor
public class DeductionTypeController {

    private final IDeductionTypeService deductionTypeService;

    @PostMapping
    public ResponseEntity<DeductionTypeDto> create(@RequestBody DeductionTypeDto deductionTypeDto) {
        return deductionTypeService.createDeductionType(deductionTypeDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeductionTypeDto> update(@PathVariable Long id, @RequestBody DeductionTypeDto deductionTypeDto) {
        return deductionTypeService.updateDeductionType(id, deductionTypeDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        return deductionTypeService.deleteDeductionType(id);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeductionTypeDto> getById(@PathVariable Long id) {
        return deductionTypeService.getDeductionTypeById(id);
    }

    @GetMapping
    public ResponseEntity<java.util.List<DeductionTypeDto>> getAll() {
        return deductionTypeService.getAllDeductionTypes();
    }
}
