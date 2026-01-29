package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.model.Employee;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EmployeeServiceImplTest {

    @Mock
    private EmployeeDao employeeDao;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    // ---------- ADD EMPLOYEE ----------

    @Test
    void shouldAddEmployeeSuccessfully() {
        EmployeeDto dto = new EmployeeDto();
        dto.setEmpCode("E001");
        dto.setRole("Manager");

        Employee savedEntity = new Employee();
        savedEntity.setEmpCode("E001");
        savedEntity.setRole("Manager");

        when(employeeDao.save(any(Employee.class))).thenReturn(savedEntity);

        EmployeeDto result = employeeService.addEmployee(dto);

        assertNotNull(result);
        assertEquals("E001", result.getEmpCode());
        verify(employeeDao, times(1)).save(any(Employee.class));
    }

    // ---------- UPDATE EMPLOYEE ----------

    @Test
    void shouldUpdateEmployeeSuccessfully() {
        EmployeeDto dto = new EmployeeDto();
        dto.setRole("Admin");

        Employee existing = new Employee();
        existing.setEmpCode("E001");

        Employee updated = new Employee();
        updated.setEmpCode("E001");
        updated.setRole("Admin");

        when(employeeDao.findByEmpCode("E001")).thenReturn(Optional.of(existing));
        when(employeeDao.save(any(Employee.class))).thenReturn(updated);

        EmployeeDto result = employeeService.updateEmployee(dto, "E001");

        assertEquals("Admin", result.getRole());
        verify(employeeDao).findByEmpCode("E001");
        verify(employeeDao).save(any(Employee.class));
    }

    @Test
    void shouldThrowExceptionWhenUpdatingNonExistingEmployee() {
        EmployeeDto dto = new EmployeeDto();

        when(employeeDao.findByEmpCode("E404")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.updateEmployee(dto, "E404")
        );

        assertTrue(exception.getMessage().contains("Employee does not exist"));
    }

    // ---------- DELETE EMPLOYEE ----------

    @Test
    void shouldDeleteEmployeeSuccessfully() {
        Employee employee = new Employee();
        employee.setEmpCode("E001");

        when(employeeDao.findByEmpCode("E001")).thenReturn(Optional.of(employee));
        doNothing().when(employeeDao).deleteByEmpCode("E001");

        employeeService.deleteEmployee("E001");

        verify(employeeDao).deleteByEmpCode("E001");
    }

    @Test
    void shouldThrowExceptionWhenDeletingNonExistingEmployee() {
        when(employeeDao.findByEmpCode("E404")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> employeeService.deleteEmployee("E404")
        );

        assertTrue(exception.getMessage().contains("Employee does not exist"));
    }

    // ---------- FETCH EMPLOYEE BY ID ----------

    @Test
    void shouldFetchEmployeeByIdSuccessfully() {
        Employee employee = new Employee();
        employee.setEmpCode("E001");

        when(employeeDao.findByEmpCode("E001")).thenReturn(Optional.of(employee));

        EmployeeDto result = employeeService.fetchEmployeeById("E001");

        assertEquals("E001", result.getEmpCode());
        verify(employeeDao).findByEmpCode("E001");
    }

    // ---------- FETCH ALL EMPLOYEES ----------

    @Test
    void shouldFetchAllEmployeesSuccessfully() {
        Employee e1 = new Employee();
        e1.setEmpCode("E001");

        Employee e2 = new Employee();
        e2.setEmpCode("E002");

        when(employeeDao.findAll()).thenReturn(List.of(e1, e2));

        List<EmployeeDto> result = employeeService.fetchAllEmployees();

        assertEquals(2, result.size());
        verify(employeeDao).findAll();
    }

    // ---------- FETCH EMPLOYEE BY ROLE ----------

    @Test
    void shouldFetchEmployeeByRoleSuccessfully() {
        Employee employee = new Employee();
        employee.setEmpCode("E001");
        employee.setRole("Manager");

        when(employeeDao.findByRole("Manager")).thenReturn(Optional.of(employee));

        List<EmployeeDto> result = employeeService.fetchEmployeeByRole("Manager");

        assertEquals(1, result.size());
        assertEquals("Manager", result.get(0).getRole());
    }
}
