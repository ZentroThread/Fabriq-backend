package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.AllowanceTypeDTO;
import com.example.FabriqBackend.service.IAllowanceTypeService;
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
    public ResponseEntity<AllowanceTypeDTO> create(@RequestBody AllowanceTypeDTO allowanceTypeDTO) {
        return allowanceTypeService.createAllowanceType(allowanceTypeDTO);
    }

    @PutMapping("/{id}")
    public ResponseEntity<AllowanceTypeDTO> update(@PathVariable Long id, @RequestBody AllowanceTypeDTO allowanceTypeDTO) {
        return allowanceTypeService.updateAllowanceType(id, allowanceTypeDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AllowanceTypeDTO> getById(@PathVariable Long id) {
        return allowanceTypeService.getAllowanceTypeById(id);
    }

    @GetMapping
    public ResponseEntity<List<AllowanceTypeDTO>> getAllAllowanceTypes() {
        return allowanceTypeService.getAllAllowanceTypes();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        return allowanceTypeService.deleteAllowanceType(id);
    }
}
