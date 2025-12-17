package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.mapper.EmployeeMapper;
import com.example.FabriqBackend.model.Employee;
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

    @CachePut(value="employees", key="#result.empCode")
    @CacheEvict(value = "employeesAll", allEntries = true)
    public EmployeeDto addEmployee(EmployeeDto dto){

        Employee emp = EmployeeMapper.toEntity(dto,new Employee());
        Employee savedEmp = empDao.save(emp);

        //EmployeeDto savedDto = EmployeeMapper.toDto(savedEmp);
        //savedDto.setAge(savedEmp.getAge());
        //savedDto.setEmpId(savedEmp.getEmpId());

        return EmployeeMapper.toDto(savedEmp);

    }

    @CachePut(value="employees", key = "#result.empCode")
    @CacheEvict(value = "employeesAll", allEntries = true)
    public EmployeeDto updateEmployee(EmployeeDto dto,String empCode){

        Employee existingEmp = empDao.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empCode));

        Employee updatedEmp = empDao.save( EmployeeMapper.toEntity(dto,existingEmp));

        return  EmployeeMapper.toDto(updatedEmp);
    }

    @Caching(evict = {
            @CacheEvict(value = "employees", key = "#empCode"),
            @CacheEvict(value = "employeesAll", allEntries = true)
    })
    public void deleteEmployee(String empCode){

        empDao.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empCode));
        empDao.deleteByEmpCode(empCode);

    }

    @Cacheable(value = "employees", key="T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':employee:' + #empCode")
    public EmployeeDto fetchEmployeeById(String empCode){

        Employee emp = empDao.findByEmpCode(empCode)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empCode));

        return EmployeeMapper.toDto(emp);
    }

    @Cacheable(value="employeesAll", key="T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allEmployees'")
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
