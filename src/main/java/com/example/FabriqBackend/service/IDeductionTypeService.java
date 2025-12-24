package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.DeductionTypeDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IDeductionTypeService {

    ResponseEntity<DeductionTypeDto> createDeductionType(DeductionTypeDto deductionTypeDto);

    ResponseEntity<DeductionTypeDto> getDeductionTypeById(Long id);

    ResponseEntity<DeductionTypeDto> updateDeductionType(Long id, DeductionTypeDto deductionTypeDto);

    ResponseEntity<Void> deleteDeductionType(Long id);

    ResponseEntity<List<DeductionTypeDto>> getAllDeductionTypes();
}
