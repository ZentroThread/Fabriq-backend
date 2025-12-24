package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.AllowanceTypeDTO;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAllowanceTypeService {

    ResponseEntity<AllowanceTypeDTO> createAllowanceType(AllowanceTypeDTO allowanceTypeDTO);

    ResponseEntity<AllowanceTypeDTO> updateAllowanceType(Long id, AllowanceTypeDTO allowanceTypeDTO);

    ResponseEntity<AllowanceTypeDTO> getAllowanceTypeById(Long id);

    ResponseEntity<List<AllowanceTypeDTO>> getAllAllowanceTypes();

    ResponseEntity<?> deleteAllowanceType(Long id);
}
