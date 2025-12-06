package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AllowanceTypeDao;
import com.example.FabriqBackend.dto.salary.AllowanceTypeDTO;
import com.example.FabriqBackend.model.salary.AllowanceType;
import com.example.FabriqBackend.service.IAllowanceTypeService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AllowanceTypeServiceImpl implements IAllowanceTypeService {

    private final AllowanceTypeDao allowanceTypeDao;
    private final ModelMapper modelMapper;


    @Override
    // Create Allowance Type
    public ResponseEntity<AllowanceTypeDTO> createAllowanceType(AllowanceTypeDTO allowanceTypeDTO) {

        AllowanceType allowanceType = modelMapper.map(allowanceTypeDTO, AllowanceType.class);
        AllowanceTypeDTO responseDTO = modelMapper.map(allowanceType, AllowanceTypeDTO.class);
        allowanceTypeDao.save(allowanceType);

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    // Update Allowance Type
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
    // Get Allowance Type by ID
    public ResponseEntity<AllowanceTypeDTO> getAllowanceTypeById(Long id) {

        AllowanceType allowanceType = allowanceTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Allowance Type not found"));

        AllowanceTypeDTO responseDTO = modelMapper.map(allowanceType, AllowanceTypeDTO.class);

        return ResponseEntity.ok(responseDTO);
    }

    @Override
    // Get All Allowance Types
    public ResponseEntity<List<AllowanceTypeDTO>> getAllAllowanceTypes() {

        List<AllowanceType> allowanceTypes = allowanceTypeDao.findAll();
        List<AllowanceTypeDTO> allowanceTypeDTOs = allowanceTypes.stream()
                .map(allowanceType -> modelMapper.map(allowanceType, AllowanceTypeDTO.class))
                .toList();

        return ResponseEntity.ok(allowanceTypeDTOs);
    }

    @Override
    // Delete Allowance Type
    public ResponseEntity<?> deleteAllowanceType(Long id) {

        AllowanceType allowanceType = allowanceTypeDao.findById(id)
                .orElseThrow(() -> new RuntimeException("Allowance Type not found"));
        allowanceTypeDao.delete(allowanceType);

        return ResponseEntity.ok().build();
    }
}
