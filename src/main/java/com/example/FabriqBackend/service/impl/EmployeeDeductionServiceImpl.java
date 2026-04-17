package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.DeductionTypeDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.EmployeeDeductionDao;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeDeductionResponseDTO;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import com.example.FabriqBackend.mapper.EmployeeDeductionMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.DeductionType;
import com.example.FabriqBackend.model.salary.EmployeeDeduction;
import com.example.FabriqBackend.service.Interface.IEmployeeDeductionService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "employeeDeductions")
public class EmployeeDeductionServiceImpl implements IEmployeeDeductionService {

    private final EmployeeDeductionDao employeeDeductionDao;
    private final EmployeeDao employeeDao;
    private final DeductionTypeDao deductionTypeDao;

    @CacheEvict(
        value = "employeeDeductions", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public EmployeeDeductionRequestDTO createEmployeeDeduction(EmployeeDeductionRequestDTO requestDTO) {

        Employee employee = employeeDao.findById(requestDTO.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(requestDTO.getEmpId())));
        DeductionType deductionType = deductionTypeDao.findById(requestDTO.getDeductionId())
                .orElseThrow(() -> new ResourceNotFoundException("DeductionType", "id", String.valueOf(requestDTO.getDeductionId())));

        EmployeeDeduction saveEmployeeDeduction = new com.example.FabriqBackend.model.salary.EmployeeDeduction();
        saveEmployeeDeduction.setEmployee(employee);
        saveEmployeeDeduction.setDeductionType(deductionType);
        employeeDeductionDao.save(saveEmployeeDeduction);

        return requestDTO;
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':empId:' + #empId")
    public List<EmployeeDeductionResponseDTO> getEmployeeDeductionsByEmpId(Long empId) {

        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(empId)));

        Optional<List<EmployeeDeduction>> employeeDeductions = employeeDeductionDao.findByEmployee_Id(empId);

        return employeeDeductions.orElse(List.of()).stream()
                .map(EmployeeDeductionMapper::toDto)
                .toList();
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allEmployeeDeductions'")
    public List<EmployeeDeductionResponseDTO> getAllEmployeeDeductions() {
        List<EmployeeDeduction> employeeDeductions = employeeDeductionDao.findAll();
        return employeeDeductions.stream()
                .map(EmployeeDeductionMapper::toDto)
                .toList();
    }

    @CacheEvict(
        value = "employeeDeductions", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public void deleteEmployeeDeduction(Long id, Long empId) {
        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(empId)));
        EmployeeDeduction employeeDeduction = employeeDeductionDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeDeduction", "id", String.valueOf(id)));
        employeeDeductionDao.deleteByIdAndEmployee_Id(id, empId);
    }
}
