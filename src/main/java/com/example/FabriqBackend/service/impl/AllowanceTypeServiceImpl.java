package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.AllowanceTypeDao;
import com.example.FabriqBackend.dto.salary.AllowanceTypeDTO;
import com.example.FabriqBackend.model.salary.AllowanceType;
import com.example.FabriqBackend.service.Interface.IAllowanceTypeService;
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
@CacheConfig(cacheNames = "allowanceTypes")
public class AllowanceTypeServiceImpl implements IAllowanceTypeService {

    private final AllowanceTypeDao allowanceTypeDao;
    private final ModelMapper modelMapper;


    @Override
    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allAllowanceTypes'")
    public ResponseEntity<AllowanceTypeDTO> createAllowanceType(AllowanceTypeDTO allowanceTypeDTO) {

        AllowanceType allowanceType = modelMapper.map(allowanceTypeDTO, AllowanceType.class);
        AllowanceTypeDTO responseDTO = modelMapper.map(allowanceType, AllowanceTypeDTO.class);
        allowanceTypeDao.save(allowanceType);

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allAllowanceTypes'")
    public ResponseEntity<AllowanceTypeDTO> updateAllowanceType(Long id, AllowanceTypeDTO allowanceTypeDTO) {

        AllowanceType allowanceType = allowanceTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Allowance Type not found"));

        AllowanceType updatedAllowanceType = modelMapper.map(allowanceTypeDTO, AllowanceType.class);
        updatedAllowanceType.setAllowanceId(allowanceType.getAllowanceId());
        allowanceTypeDao.save(updatedAllowanceType);

        AllowanceTypeDTO responseDTO = modelMapper.map(updatedAllowanceType, AllowanceTypeDTO.class);

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    public ResponseEntity<AllowanceTypeDTO> getAllowanceTypeById(Long id) {

        AllowanceType allowanceType = allowanceTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Allowance Type not found"));

        AllowanceTypeDTO responseDTO = modelMapper.map(allowanceType, AllowanceTypeDTO.class);

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allAllowanceTypes'")
    public ResponseEntity<List<AllowanceTypeDTO>> getAllAllowanceTypes() {

        List<AllowanceType> allowanceTypes = allowanceTypeDao.findAll();
        List<AllowanceTypeDTO> allowanceTypeDTOs = allowanceTypes.stream()
                .map(allowanceType -> modelMapper.map(allowanceType, AllowanceTypeDTO.class))
                .toList();

        return ResponseEntity.ok(allowanceTypeDTOs);
    }

    @Override
    @CacheEvict(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allAllowanceTypes'")
    public ResponseEntity<?> deleteAllowanceType(Long id) {

        AllowanceType allowanceType = allowanceTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Allowance Type not found"));
        allowanceTypeDao.delete(allowanceType);

        return ResponseEntity.ok().build();
    }
}
