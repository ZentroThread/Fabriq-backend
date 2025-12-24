package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.EmployeeBankDetailsDao;
import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.mapper.EmployeeMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.EmployeeBankDetails;
import com.example.FabriqBackend.service.IEmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class EmployeeServiceImpl implements IEmployeeService {

    private final EmployeeDao empDao;
    private final EmployeeBankDetailsDao empBankDao;

    @CachePut(
            value = "employees",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #result.empCode"
    )
    @CacheEvict(
            value = "employeesAll",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant()"
    )
    public EmployeeDto addEmployee(EmployeeDto dto){

        Employee emp = EmployeeMapper.toEntity(dto,new Employee());
        Employee savedEmp = empDao.save(emp);

        //EmployeeDto savedDto = EmployeeMapper.toDto(savedEmp);
        //savedDto.setAge(savedEmp.getAge());
        //savedDto.setEmpId(savedEmp.getEmpId());

        return EmployeeMapper.toDto(savedEmp);

    }

    @CachePut(
            value = "employees",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #result.empCode"
    )
    @CacheEvict(
            value = "employeesAll",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant()"
    )
    public EmployeeDto updateEmployee(EmployeeDto dto,String empCode){

        Employee existingEmp = empDao.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empCode));

        EmployeeMapper.toEntity(dto,existingEmp);

        if (existingEmp.getEmployeeBankDetails() != null) {
            empBankDao.save(existingEmp.getEmployeeBankDetails());
        }
        Employee updatedEmp = empDao.save(existingEmp);

        return EmployeeMapper.toDto(updatedEmp);
    }

    @Caching(evict = {
            @CacheEvict(
                    value = "employees",
                    key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #empCode"
            ),
            @CacheEvict(
                    value = "employeesAll",
                    key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant()"
            )
    })
    public void deleteEmployee(String empCode){

        Employee emp = empDao.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empCode));
        EmployeeBankDetails bankDetails = emp.getEmployeeBankDetails();
        if(bankDetails != null){
            empBankDao.deleteById(bankDetails.getId());
        }
        empDao.deleteByEmpCode(empCode);

    }

    @Cacheable(
            value = "employees",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #empCode"
    )
    public EmployeeDto fetchEmployeeById(String empCode){

        Employee emp = empDao.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empCode));

        return EmployeeMapper.toDto(emp);
    }

    @Cacheable(
            value = "employeesAll",
            key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant()"
    )
    public List<EmployeeDto> fetchAllEmployees(){
        List<Employee> empList = empDao.findAll();

        return empList
                .stream()
                .map(EmployeeMapper::toDto)
                .toList();
    }

    public List<EmployeeDto> fetchEmployeeByRole(String role){

        Optional<Employee> empList = empDao.findByRole(role);

        return empList
                .stream()
                .map(EmployeeMapper::toDto)
                .toList();
    }
}
