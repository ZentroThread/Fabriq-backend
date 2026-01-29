package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.*;
import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;
import com.example.FabriqBackend.enums.AllowanceTypeEnum;
import com.example.FabriqBackend.enums.DeductionTypeEnum;
import com.example.FabriqBackend.model.Attendance;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.*;
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
class PayrollCalculationServiceImplTest {

    @Mock private PayrollRecordDao payrollRecordDao;
    @Mock private EmployeeDao employeeDao;
    @Mock private AttendanceDao attendanceDao;
    @Mock private EmployeeDeductionDao employeeDeductionDao;
    @Mock private EmployeeAllowanceDao employeeAllowanceDao;
    @Mock private ProductionRecordDao productionRecordDao;

    @InjectMocks
    private PayrollCalculationServiceImpl service;

    private Employee employee;

    @BeforeEach
    void setUp() {
        employee = new Employee();
        employee.setId(1L);
        employee.setEmpCode("EMP001");
        employee.setEmpFirstName("John");
        employee.setEmpLastName("Doe");
        employee.setBasicSalary(100_000.0);
    }

    // ---------------- CALCULATE PAYROLL ----------------

    @Test
    void shouldCalculatePayrollSuccessfully() {

        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));

        // Attendance (170 hours → 10 OT hours)
        Attendance attendance = new Attendance();
        attendance.setTotalHours(170.0);

        when(attendanceDao.findByEmployee_IdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(attendance)));

        // Allowance
        AllowanceType allowanceType = new AllowanceType();
        allowanceType.setType(AllowanceTypeEnum.FIXED);
        allowanceType.setAmount(5000.0);

        EmployeeAllowance allowance = new EmployeeAllowance();
        allowance.setAllowanceType(allowanceType);

        when(employeeAllowanceDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of(allowance)));

        // Deduction
        DeductionType deductionType = new DeductionType();
        deductionType.setType(DeductionTypeEnum.FIXED);
        deductionType.setAmount(2000.0);

        EmployeeDeduction deduction = new EmployeeDeduction();
        deduction.setDeductionType(deductionType);

        when(employeeDeductionDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of(deduction)));

        // Production
        ProductionRecord pr = new ProductionRecord();
        pr.setQuantity(10);
        pr.setRatePerProduct(100.0);

        when(productionRecordDao.findByEmployee_IdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of(pr)));

        PayrollResponseDTO result =
                service.calculatePayroll(1L, 8, 2024);

        assertNotNull(result);
        assertEquals("EMP001", result.getEmpCode());
        assertEquals(100_000.0, result.getBasicSalary());
        assertTrue(result.getGrossSalary() > 0);
        assertTrue(result.getNetSalary() > 0);
    }

    // ---------------- CONFIRM & SAVE ----------------

    @Test
    void shouldConfirmAndSavePayroll() {

        when(employeeDao.findById(1L)).thenReturn(Optional.of(employee));

        when(attendanceDao.findByEmployee_IdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of()));

        when(employeeAllowanceDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of()));

        when(employeeDeductionDao.findByEmployee_Id(1L))
                .thenReturn(Optional.of(List.of()));

        when(productionRecordDao.findByEmployee_IdAndDateBetween(anyLong(), any(), any()))
                .thenReturn(Optional.of(List.of()));

        PayrollResponseDTO result =
                service.confirmAndSave(1L, 8, 2024);

        assertNotNull(result);
        verify(payrollRecordDao, times(1)).save(any(PayrollRecord.class));
    }

    // ---------------- NEGATIVE ----------------

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        when(employeeDao.findById(1L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> service.calculatePayroll(1L, 8, 2024));

        assertEquals("Employee not found", ex.getMessage());
    }
}
