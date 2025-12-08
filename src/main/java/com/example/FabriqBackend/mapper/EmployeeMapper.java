package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.EmployeeBankDetails;

import java.time.LocalDate;
import java.time.Period;

public class EmployeeMapper {

    // ENTITY → DTO
    public static EmployeeDto toDto(Employee employee) {
        if (employee == null) return null;

        EmployeeDto dto = new EmployeeDto();

        dto.setId(employee.getId());
        dto.setEmpCode(employee.getEmpCode());
        dto.setEmpFirstName(employee.getEmpFirstName());
        dto.setEmpLastName(employee.getEmpLastName());
        dto.setNicNumber(employee.getNicNumber());
        dto.setMobileNumber(employee.getMobileNumber());
        dto.setDateOfBirth(employee.getDateOfBirth());
        dto.setRole(employee.getRole());
        dto.setAddress(employee.getAddress());
        dto.setGender(employee.getGender());
        dto.setJoinedDate(employee.getJoinedDate());
        dto.setEpfNumber(employee.getEpfNumber());

        if(employee.getEmployeeBankDetails() != null) {
            dto.setEmployeeBankDetails(EmployeeBankDetailsMapper.toDto(employee.getEmployeeBankDetails()));
        }

        if (employee.getDateOfBirth() != null) {
            dto.setAge(calculateAge(employee.getDateOfBirth()));
        }

        return dto;
    }

    // DTO → ENTITY
    public static Employee toEntity(EmployeeDto dto, Employee employee) {
        if (dto == null) return null;

        employee.setEmpCode(dto.getEmpCode());
        employee.setEmpFirstName(dto.getEmpFirstName());
        employee.setEmpLastName(dto.getEmpLastName());
        employee.setNicNumber(dto.getNicNumber());
        employee.setMobileNumber(dto.getMobileNumber());
        employee.setDateOfBirth(dto.getDateOfBirth());
        employee.setRole(dto.getRole());
        employee.setAddress(dto.getAddress());
        employee.setGender(dto.getGender());
        employee.setJoinedDate(dto.getJoinedDate());
        employee.setEpfNumber(dto.getEpfNumber());

        if( dto.getEmployeeBankDetails() != null) {
            employee.setEmployeeBankDetails(EmployeeBankDetailsMapper.toEntity(dto.getEmployeeBankDetails(),
                    employee.getEmployeeBankDetails() != null ? employee.getEmployeeBankDetails() : new EmployeeBankDetails()));
        }

        if( dto.getDateOfBirth() != null) {
            employee.setDateOfBirth(dto.getDateOfBirth());
        }

        return employee;
    }

    // AGE calculation
    private static Integer calculateAge(String dob) {
        LocalDate date = LocalDate.parse(dob);
        return Period.between(date, LocalDate.now()).getYears();
    }

}
