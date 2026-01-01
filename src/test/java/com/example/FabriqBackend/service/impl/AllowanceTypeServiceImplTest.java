package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AllowanceTypeDao;
import com.example.FabriqBackend.dto.salary.AllowanceTypeDTO;
import com.example.FabriqBackend.model.salary.AllowanceType;
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
class AllowanceTypeServiceImplTest {

    @Mock
    private AllowanceTypeDao allowanceTypeDao;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AllowanceTypeServiceImpl allowanceTypeService;

    private AllowanceType allowanceType;
    private AllowanceTypeDTO allowanceTypeDTO;

    @BeforeEach
    void setUp() {
        allowanceType = new AllowanceType();
        allowanceType.setAllowanceId(1L);

        allowanceTypeDTO = new AllowanceTypeDTO();

    }

    // ---------- CREATE ----------

    @Test
    void shouldCreateAllowanceTypeSuccessfully() {
        when(modelMapper.map(allowanceTypeDTO, AllowanceType.class))
                .thenReturn(allowanceType);
        when(modelMapper.map(allowanceType, AllowanceTypeDTO.class))
                .thenReturn(allowanceTypeDTO);

        ResponseEntity<AllowanceTypeDTO> response =
                allowanceTypeService.createAllowanceType(allowanceTypeDTO);

        assertNotNull(response.getBody());
        verify(allowanceTypeDao).save(allowanceType);
    }

    // ---------- UPDATE ----------

    @Test
    void shouldUpdateAllowanceTypeSuccessfully() {
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.of(allowanceType));
        when(modelMapper.map(allowanceTypeDTO, AllowanceType.class))
                .thenReturn(allowanceType);
        when(modelMapper.map(allowanceType, AllowanceTypeDTO.class))
                .thenReturn(allowanceTypeDTO);

        ResponseEntity<AllowanceTypeDTO> response =
                allowanceTypeService.updateAllowanceType(1L, allowanceTypeDTO);

        assertNotNull(response.getBody());
        verify(allowanceTypeDao).save(any(AllowanceType.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingAllowanceType() {
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> allowanceTypeService.updateAllowanceType(1L, allowanceTypeDTO)
        );
    }

    // ---------- READ ----------

    @Test
    void shouldReturnAllowanceTypeById() {
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.of(allowanceType));
        when(modelMapper.map(allowanceType, AllowanceTypeDTO.class))
                .thenReturn(allowanceTypeDTO);

        ResponseEntity<AllowanceTypeDTO> response =
                allowanceTypeService.getAllowanceTypeById(1L);

        assertNotNull(response.getBody());
    }

    @Test
    void shouldReturnAllAllowanceTypes() {
        when(allowanceTypeDao.findAll())
                .thenReturn(List.of(allowanceType));
        when(modelMapper.map(allowanceType, AllowanceTypeDTO.class))
                .thenReturn(allowanceTypeDTO);

        ResponseEntity<List<AllowanceTypeDTO>> response =
                allowanceTypeService.getAllAllowanceTypes();

        assertEquals(1, response.getBody().size());
        verify(allowanceTypeDao).findAll();
    }

    // ---------- DELETE ----------

    @Test
    void shouldDeleteAllowanceTypeSuccessfully() {
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.of(allowanceType));

        ResponseEntity<?> response =
                allowanceTypeService.deleteAllowanceType(1L);

        assertEquals(200, response.getStatusCodeValue());
        verify(allowanceTypeDao).delete(allowanceType);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingAllowanceType() {
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> allowanceTypeService.deleteAllowanceType(1L)
        );
    }
}
