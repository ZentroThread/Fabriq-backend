package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.DeductionTypeDao;
import com.example.FabriqBackend.dto.salary.DeductionTypeDto;
import com.example.FabriqBackend.model.salary.DeductionType;
import com.example.FabriqBackend.service.IDeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeductionTypeServiceImpl implements IDeductionTypeService {

    private final DeductionTypeDao deductionTypeDao;
    private final ModelMapper modelMapper;



    @Override
    // Create Deduction Type
    public ResponseEntity<DeductionTypeDto> createDeductionType(DeductionTypeDto deductionTypeDto) {

        DeductionType deductionType = modelMapper.map(deductionTypeDto, DeductionType.class);
        deductionTypeDao.save(deductionType);

        return ResponseEntity.ok(deductionTypeDto);
    }

    @Override
    // Get Deduction Type by ID
    public ResponseEntity<DeductionTypeDto> getDeductionTypeById(Long id) {

        DeductionType deductionType = deductionTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction Type not found with id: " + id));
        DeductionTypeDto deductionTypeDto = modelMapper.map(deductionType, DeductionTypeDto.class);

        return ResponseEntity.ok(deductionTypeDto);
    }

    @Override
    // Update Deduction Type
    public ResponseEntity<DeductionTypeDto> updateDeductionType(Long id, DeductionTypeDto deductionTypeDto) {

        DeductionType deductionType = deductionTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction Type not found with id: " + id));

        DeductionType updatedDeductionType = modelMapper.map(deductionTypeDto, DeductionType.class);
        updatedDeductionType.setDeductionId(deductionType.getDeductionId());
        deductionTypeDao.save(updatedDeductionType);

        return ResponseEntity.ok(deductionTypeDto);
    }

    @Override
    // Delete Deduction Type
    public ResponseEntity<Void> deleteDeductionType(Long id) {

        DeductionType deductionType = deductionTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Deduction Type not found with id: " + id));

        deductionTypeDao.delete(deductionType);

        return ResponseEntity.ok().build();
    }

    @Override
    // Get All Deduction Types
    public ResponseEntity<List<DeductionTypeDto>> getAllDeductionTypes() {

        List<DeductionType> deductionTypes = deductionTypeDao.findAll();
        List<DeductionTypeDto> deductionTypeDtos = deductionTypes.stream()
                .map(deductionType -> modelMapper.map(deductionType, DeductionTypeDto.class))
                .toList();

        return ResponseEntity.ok(deductionTypeDtos);
    }
}
