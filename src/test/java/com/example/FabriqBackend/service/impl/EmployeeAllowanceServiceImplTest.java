package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AllowanceTypeDao;
import com.example.FabriqBackend.dao.EmployeeAllowanceDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceResponseDTO;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AllowanceType;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import com.example.FabriqBackend.enums.AllowanceTypeEnum;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeAllowanceServiceImplTest {

    @Mock
    private EmployeeDao employeeDao;

    @Mock
    private AllowanceTypeDao allowanceTypeDao;

    @Mock
    private EmployeeAllowanceDao employeeAllowanceDao;

    @InjectMocks
    private EmployeeAllowanceServiceImpl employeeAllowanceService;

    private Employee employee;
    private AllowanceType allowanceType;
    private EmployeeAllowance employeeAllowance;


    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);

        allowanceType = new AllowanceType();
        allowanceType.setAllowanceId(1L);
        allowanceType.setType(AllowanceTypeEnum.FIXED);

        employeeAllowance = new EmployeeAllowance();
        employeeAllowance.setEmployee(employee);
        employeeAllowance.setAllowanceType(allowanceType);
    }


    // ---------------- CREATE ----------------

    @Test
    void shouldCreateEmployeeAllowanceSuccessfully() {
        EmployeeAllowanceRequestDTO dto = new EmployeeAllowanceRequestDTO();
        dto.setEmpId(1L);
        dto.setAllowanceId(1L);

        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.of(allowanceType));

        EmployeeAllowanceRequestDTO result =
                employeeAllowanceService.createEmployeeAllowance(dto);

        assertNotNull(result);
        verify(employeeAllowanceDao).save(any(EmployeeAllowance.class));
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        EmployeeAllowanceRequestDTO dto = new EmployeeAllowanceRequestDTO();
        dto.setEmpId(1L);
        dto.setAllowanceId(1L);

        when(employeeDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> employeeAllowanceService.createEmployeeAllowance(dto)
        );
    }

    @Test
    void shouldThrowExceptionWhenAllowanceTypeNotFound() {
        EmployeeAllowanceRequestDTO dto = new EmployeeAllowanceRequestDTO();
        dto.setEmpId(1L);
        dto.setAllowanceId(1L);

        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));
        when(allowanceTypeDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> employeeAllowanceService.createEmployeeAllowance(dto)
        );
    }

    // ---------------- GET BY EMPLOYEE ----------------

    @Test
    void shouldReturnEmployeeAllowancesByEmpId() {
        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));
        when(employeeAllowanceDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of(employeeAllowance)));

        List<EmployeeAllowanceResponseDTO> result =
                employeeAllowanceService.getEmployeeAllowancesByEmpId(1L);

        assertEquals(1, result.size());
    }

    @Test
    void shouldReturnEmptyListWhenNoAllowancesFound() {
        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));
        when(employeeAllowanceDao.findByEmployee_Id(1L))
                .thenReturn(Optional.empty());

        List<EmployeeAllowanceResponseDTO> result =
                employeeAllowanceService.getEmployeeAllowancesByEmpId(1L);

        assertTrue(result.isEmpty());
    }

    // ---------------- GET ALL ----------------

    @Test
    void shouldReturnAllEmployeeAllowances() {
        when(employeeAllowanceDao.findAll())
                .thenReturn(List.of(employeeAllowance));

        List<EmployeeAllowanceResponseDTO> result =
                employeeAllowanceService.getAllEmployeeAllowances();

        assertEquals(1, result.size());
        verify(employeeAllowanceDao).findAll();
    }

    // ---------------- DELETE ----------------

    @Test
    void shouldDeleteEmployeeAllowanceSuccessfully() {
        when(employeeDao.findById(1L))
                .thenReturn(Optional.of(employee));
        when(employeeAllowanceDao.findById(1L))
                .thenReturn(Optional.of(employeeAllowance));

        employeeAllowanceService.deleteEmployeeAllowance(1L, 1L);

        verify(employeeAllowanceDao)
                .deleteByIdAndEmployee_Id(1L, 1L);
    }

    @Test
    void shouldThrowExceptionWhenDeletingWithInvalidEmployee() {
        when(employeeDao.findById(1L))
                .thenReturn(Optional.empty());

        assertThrows(
                RuntimeException.class,
                () -> employeeAllowanceService.deleteEmployeeAllowance(1L, 1L)
        );
    }
}
