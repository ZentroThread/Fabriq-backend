package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.ProductionRecordDao;
import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.ProductionRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductionRecordServiceImplTest {

    @Mock
    private ProductionRecordDao productionRecordDao;

    @Mock
    private EmployeeDao employeeDao;

    @InjectMocks
    private ProductionRecordServiceImpl service;

    private Employee employee;
    private ProductionRecord record;
    private ProductionRecordRequestDTO requestDTO;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmpCode("EMP001");

        record = new ProductionRecord();
        record.setId(1L);
        record.setDate(LocalDate.now());
        record.setQuantity(10);
        record.setRatePerProduct(100.0);
        record.setEmployee(employee);

        requestDTO = new ProductionRecordRequestDTO();
        requestDTO.setEmpId(1L);
        requestDTO.setDate(LocalDate.parse(LocalDate.now().toString()));
        requestDTO.setQuantity(10);
        requestDTO.setRatePerProduct(100.0);
    }

    // ---------------- CREATE ----------------

    @Test
    void shouldCreateProductionRecord() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));

        ProductionRecordRequestDTO result =
                service.createProductionRecord(requestDTO);

        assertNotNull(result);
        verify(productionRecordDao, times(1)).save(any(ProductionRecord.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingRecordIfEmployeeNotFound() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createProductionRecord(requestDTO));

        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    // ---------------- GET BY EMPLOYEE ----------------

    @Test
    void shouldReturnRecordsByEmployeeId() {
        when(productionRecordDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of(record)));

        List<ProductionRecordResponseDTO> result =
                service.getRecordsByEmployeeId(1L);

        assertEquals(1, result.size());
    }

    // ---------------- GET BY DATE RANGE ----------------

    @Test
    void shouldReturnRecordsByDateRange() {
        when(productionRecordDao.findByDateBetween(any(), any()))
                .thenReturn(Optional.of(List.of(record)));

        List<ProductionRecordResponseDTO> result =
                service.getRecordsByDateRange("2024-01-01", "2024-01-31");

        assertFalse(result.isEmpty());
    }

    // ---------------- GET ALL ----------------

    @Test
    void shouldReturnAllProductionRecords() {
        when(productionRecordDao.findAll())
                .thenReturn(List.of(record));

        List<ProductionRecordResponseDTO> result =
                service.getAllProductionRecords();

        assertEquals(1, result.size());
    }

    // ---------------- UPDATE ----------------

    @Test
    void shouldUpdateProductionRecord() {
        when(productionRecordDao.findById(1L))
                .thenReturn(Optional.of(record));
        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));

        ProductionRecordResponseDTO result =
                service.updateProductionRecord(1L, requestDTO);

        assertNotNull(result);
        verify(productionRecordDao, times(1)).save(any(ProductionRecord.class));
    }

    // ---------------- DELETE ----------------

    @Test
    void shouldDeleteProductionRecord() {
        when(productionRecordDao.findById(1L))
                .thenReturn(Optional.of(record));

        service.deleteProductionRecordById(1L);

        verify(productionRecordDao, times(1)).deleteById(1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingRecord() {
        when(productionRecordDao.findById(1L))
                .thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deleteProductionRecordById(1L));

        assertTrue(ex.getMessage().contains("Production record not found"));
    }
}
