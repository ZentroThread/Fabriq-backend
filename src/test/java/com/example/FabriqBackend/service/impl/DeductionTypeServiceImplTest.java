package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.DeductionTypeDao;
import com.example.FabriqBackend.dto.salary.DeductionTypeDto;
import com.example.FabriqBackend.model.salary.DeductionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeductionTypeServiceImplTest {

    @Mock
    private DeductionTypeDao deductionTypeDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private DeductionTypeServiceImpl deductionTypeService;

    private DeductionType deductionType;
    private DeductionTypeDto deductionTypeDto;

    @BeforeEach
    void setUp() {
        deductionType = new DeductionType();
        deductionType.setDeductionId(1L); // ✅ safe – exists

        deductionTypeDto = new DeductionTypeDto();
        // ❌ no assumptions about DTO fields
    }

    // ---------- CREATE ----------

    @Test
    void shouldCreateDeductionTypeSuccessfully() {
        when(modelMapper.map(deductionTypeDto, DeductionType.class))
                .thenReturn(deductionType);

        ResponseEntity<DeductionTypeDto> response =
                deductionTypeService.createDeductionType(deductionTypeDto);

        assertNotNull(response.getBody());
        verify(deductionTypeDao).save(deductionType);
    }

    // ---------- READ BY ID ----------

    @Test
    void shouldReturnDeductionTypeById() {
        when(deductionTypeDao.findById(1L))
                .thenReturn(Optional.of(deductionType));
        when(modelMapper.map(deductionType, DeductionTypeDto.class))
                .thenReturn(deductionTypeDto);

        ResponseEntity<DeductionTypeDto> response =
                deductionTypeService.getDeductionTypeById(1L);

        assertNotNull(response.getBody());
    }

    @Test
    void shouldThrowExceptionWhenDeductionTypeNotFound() {
        when(deductionTypeDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> deductionTypeService.getDeductionTypeById(1L)
        );
    }

    // ---------- UPDATE ----------

    @Test
    void shouldUpdateDeductionTypeSuccessfully() {
        when(deductionTypeDao.findById(1L))
                .thenReturn(Optional.of(deductionType));
        when(modelMapper.map(deductionTypeDto, DeductionType.class))
                .thenReturn(deductionType);

        ResponseEntity<DeductionTypeDto> response =
                deductionTypeService.updateDeductionType(1L, deductionTypeDto);

        assertNotNull(response.getBody());
        verify(deductionTypeDao).save(any(DeductionType.class));
    }

    // ---------- DELETE ----------

    @Test
    void shouldDeleteDeductionTypeSuccessfully() {
        when(deductionTypeDao.findById(1L))
                .thenReturn(Optional.of(deductionType));

        ResponseEntity<Void> response =
                deductionTypeService.deleteDeductionType(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(deductionTypeDao).delete(deductionType);
    }

    // ---------- READ ALL ----------

    @Test
    void shouldReturnAllDeductionTypes() {
        when(deductionTypeDao.findAll())
                .thenReturn(List.of(deductionType));
        when(modelMapper.map(deductionType, DeductionTypeDto.class))
                .thenReturn(deductionTypeDto);

        ResponseEntity<List<DeductionTypeDto>> response =
                deductionTypeService.getAllDeductionTypes();

        assertEquals(1, response.getBody().size());
        verify(deductionTypeDao).findAll();
    }
}
