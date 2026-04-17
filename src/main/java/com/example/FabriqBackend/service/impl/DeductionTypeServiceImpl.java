package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.DeductionTypeDao;
import com.example.FabriqBackend.dto.salary.DeductionTypeDto;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import com.example.FabriqBackend.model.salary.DeductionType;
import com.example.FabriqBackend.service.Interface.IDeductionTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "deductionTypes")
public class DeductionTypeServiceImpl implements IDeductionTypeService {

    private final DeductionTypeDao deductionTypeDao;
    private final ModelMapper modelMapper;


    @Override
    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allDeductionTypes'")
    public ResponseEntity<DeductionTypeDto> createDeductionType(DeductionTypeDto deductionTypeDto) {

        DeductionType deductionType = modelMapper.map(deductionTypeDto, DeductionType.class);
        deductionTypeDao.save(deductionType);

        return ResponseEntity.ok(deductionTypeDto);
    }

    @Override
    public ResponseEntity<DeductionTypeDto> getDeductionTypeById(Long id) {

        DeductionType deductionType = deductionTypeDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction Type", "id", String.valueOf(id)));
        DeductionTypeDto deductionTypeDto = modelMapper.map(deductionType, DeductionTypeDto.class);

        return ResponseEntity.ok(deductionTypeDto);
    }

    @Override
    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allDeductionTypes'")
    public ResponseEntity<DeductionTypeDto> updateDeductionType(Long id, DeductionTypeDto deductionTypeDto) {

        DeductionType deductionType = deductionTypeDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction Type", "id", String.valueOf(id)));

        DeductionType updatedDeductionType = modelMapper.map(deductionTypeDto, DeductionType.class);
        updatedDeductionType.setDeductionId(deductionType.getDeductionId());
        deductionTypeDao.save(updatedDeductionType);

        return ResponseEntity.ok(deductionTypeDto);
    }

    @Override
    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allDeductionTypes'")
    public ResponseEntity<Void> deleteDeductionType(Long id) {

        DeductionType deductionType = deductionTypeDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Deduction Type", "id", String.valueOf(id)));

        deductionTypeDao.delete(deductionType);

        return ResponseEntity.ok().build();
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allDeductionTypes'")
    public ResponseEntity<List<DeductionTypeDto>> getAllDeductionTypes() {

        List<DeductionType> deductionTypes = deductionTypeDao.findAll();
        List<DeductionTypeDto> deductionTypeDtos = deductionTypes.stream()
                .map(deductionType -> modelMapper.map(deductionType, DeductionTypeDto.class))
                .toList();

        return ResponseEntity.ok(deductionTypeDtos);
    }
}
