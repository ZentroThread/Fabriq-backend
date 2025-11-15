package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.mapper.EmployeeMapper;
import com.example.FabriqBackend.model.Employee;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class EmployeeService {

    private final EmployeeDao empDao;

    @CachePut(value="employees", key="#result.empId")
    @CacheEvict(value = "employeesAll", allEntries = true)
    public EmployeeDto addEmployee(EmployeeDto dto){

        Employee emp = EmployeeMapper.toEntity(dto,new Employee());
        Employee savedEmp = empDao.save(emp);

        EmployeeDto savedDto = EmployeeMapper.toDto(savedEmp);
        //savedDto.setAge(savedEmp.getAge());
        savedDto.setEmpId(savedEmp.getEmpId());

        return savedDto;

    }

    @CachePut(value="employees", key = "#result.empId")
    @CacheEvict(value = "employeesAll", allEntries = true)
    public EmployeeDto updateEmployee(EmployeeDto dto,Long empId){

        Employee existingEmp = empDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empId));

        Employee updatedEmp = empDao.save( EmployeeMapper.toEntity(dto,existingEmp));

        return  EmployeeMapper.toDto(updatedEmp);
    }

    @Caching(evict = {
            @CacheEvict(value = "employees", key = "#empId"),
            @CacheEvict(value = "employeesAll", allEntries = true)
    })
    public void deleteEmployee(Long empId){

        empDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empId));
        empDao.deleteById(empId);

    }

    @Cacheable(value = "employees", key="#empId")
    public EmployeeDto fetchEmployeeById(Long empId){

        Employee emp = empDao.findById(empId)
                .orElseThrow(() -> new RuntimeException("Employee does not exist with id: " + empId));

        return EmployeeMapper.toDto(emp);
    }

    @Cacheable(value="employeesAll")
    public List<EmployeeDto> fetchAllEmployees(){
        List<Employee> empList = empDao.findAll();

        return empList
                .stream()
                .map(EmployeeMapper::toDto)
                .toList();
    }
}
