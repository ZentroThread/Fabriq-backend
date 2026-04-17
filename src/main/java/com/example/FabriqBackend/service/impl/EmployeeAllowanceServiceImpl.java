package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.AllowanceTypeDao;
import com.example.FabriqBackend.dao.EmployeeAllowanceDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceRequestDTO;
import com.example.FabriqBackend.dto.salary.EmployeeAllowanceResponseDTO;
import com.example.FabriqBackend.mapper.EmployeeAllowanceMapper;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.AllowanceType;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;
import com.example.FabriqBackend.service.Interface.IEmployeeAllowanceService;
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
@CacheConfig(cacheNames = "employeeAllowances")
public class EmployeeAllowanceServiceImpl implements IEmployeeAllowanceService {

    private final EmployeeDao employeeDao;
    private final AllowanceTypeDao allowanceTypeDao;
    private final EmployeeAllowanceDao employeeAllowanceDao;

    @CacheEvict(
        value = "employeeAllowances", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public EmployeeAllowanceRequestDTO createEmployeeAllowance(EmployeeAllowanceRequestDTO requestDTO) {

        Employee employee = employeeDao.findById(requestDTO.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(requestDTO.getEmpId())));
        AllowanceType allowanceType = allowanceTypeDao.findById(requestDTO.getAllowanceId())
                .orElseThrow(() -> new ResourceNotFoundException("AllowanceType", "id", String.valueOf(requestDTO.getAllowanceId())));

        EmployeeAllowance saveEmployeeAllowance = new EmployeeAllowance();
        saveEmployeeAllowance.setEmployee(employee);
        saveEmployeeAllowance.setAllowanceType(allowanceType);

        employeeAllowanceDao.save(saveEmployeeAllowance);

        return requestDTO;
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':empId:' + #empId")
    public List<EmployeeAllowanceResponseDTO> getEmployeeAllowancesByEmpId(Long empId) {

        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(empId)));

        Optional<List<EmployeeAllowance>> employeeAllowances = employeeAllowanceDao.findByEmployee_Id(empId);

        return employeeAllowances.orElse(List.of()).stream()
                .map(EmployeeAllowanceMapper::toDto)
                .toList();
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allEmployeeAllowances'")
    public List<EmployeeAllowanceResponseDTO> getAllEmployeeAllowances() {

        List<EmployeeAllowance> employeeAllowances = employeeAllowanceDao.findAll();

        return employeeAllowances.stream()
                .map(EmployeeAllowanceMapper::toDto)
                .toList();

    }

    @CacheEvict(
        value = "employeeAllowances", 
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public void deleteEmployeeAllowance(Long id, Long empId) {
        Employee employee = employeeDao.findById(empId)
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(empId)));
        EmployeeAllowance employeeAllowance = employeeAllowanceDao.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("EmployeeAllowance", "id", String.valueOf(id)));
        employeeAllowanceDao.deleteByIdAndEmployee_Id(id, empId);
    }

}
