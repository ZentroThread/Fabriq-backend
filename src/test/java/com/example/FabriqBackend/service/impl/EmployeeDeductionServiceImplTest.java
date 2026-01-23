package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.DeductionTypeDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.EmployeeDeductionDao;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionRequestDTO;
import com.example.FabriqBackend.enums.DeductionTypeEnum;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.DeductionType;
import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeDeductionServiceImplTest {

    @Mock
    private EmployeeDeductionDao employeeDeductionDao;

    @Mock
    private EmployeeDao employeeDao;

    @Mock
    private DeductionTypeDao deductionTypeDao;

    @InjectMocks
    private EmployeeDeductionServiceImpl service;

    private Employee employee;
    private DeductionType deductionType;
    private EmployeeDeduction employeeDeduction;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);

        deductionType = new DeductionType();
        deductionType.setDeductionId(1L);
        deductionType.setType(DeductionTypeEnum.FIXED); // ✅ IMPORTANT

        employeeDeduction = new EmployeeDeduction();
        employeeDeduction.setEmployee(employee);
        employeeDeduction.setDeductionType(deductionType);
    }

    // ---------------- CREATE ----------------

    @Test
    void shouldCreateEmployeeDeductionSuccessfully() {
        EmployeeDeductionRequestDTO dto = new EmployeeDeductionRequestDTO();
        dto.setEmpId(1L);
        dto.setDeductionId(1L);

        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));
        when(deductionTypeDao.findById(1L)).thenReturn(Optional.of(deductionType));

        EmployeeDeductionRequestDTO result = service.createEmployeeDeduction(dto);

        assertNotNull(result);
        verify(employeeDeductionDao).save(any(EmployeeDeduction.class));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFoundOnCreate() {
        EmployeeDeductionRequestDTO dto = new EmployeeDeductionRequestDTO();
        dto.setEmpId(1L);
        dto.setDeductionId(1L);

        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createEmployeeDeduction(dto));

        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    @Test
    void shouldThrowExceptionWhenDeductionTypeNotFoundOnCreate() {
        EmployeeDeductionRequestDTO dto = new EmployeeDeductionRequestDTO();
        dto.setEmpId(1L);
        dto.setDeductionId(1L);

        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));
        when(deductionTypeDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.createEmployeeDeduction(dto));

        assertTrue(ex.getMessage().contains("DeductionType not found"));
    }

    // ---------------- GET BY EMPLOYEE ----------------

    @Test
    void shouldReturnEmployeeDeductionsByEmpId() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeDeductionDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of(employeeDeduction)));

        List<?> result = service.getEmployeeDeductionsByEmpId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFoundOnGetByEmpId() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.getEmployeeDeductionsByEmpId(1L));

        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    // ---------------- GET ALL ----------------

    @Test
    void shouldReturnAllEmployeeDeductions() {
        when(employeeDeductionDao.findAll()).thenReturn(List.of(employeeDeduction));

        List<?> result = service.getAllEmployeeDeductions();

        assertEquals(1, result.size());
    }

    // ---------------- DELETE ----------------

    @Test
    void shouldDeleteEmployeeDeductionSuccessfully() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeDeductionDao.findById(1L)).thenReturn(Optional.of(employeeDeduction));

        service.deleteEmployeeDeduction(1L, 1L);

        verify(employeeDeductionDao).deleteByIdAndEmployee_Id(1L, 1L);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFoundOnDelete() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deleteEmployeeDeduction(1L, 1L));

        assertTrue(ex.getMessage().contains("Employee not found"));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeDeductionNotFoundOnDelete() {
        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));
        when(employeeDeductionDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.deleteEmployeeDeduction(1L, 1L));

        assertTrue(ex.getMessage().contains("EmployeeDeduction not found"));
    }
}
