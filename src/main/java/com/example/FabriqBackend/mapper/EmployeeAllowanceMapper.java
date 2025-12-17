package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.EmployeeAllowanceResponseDTO;
import com.example.FabriqBackend.model.salary.EmployeeAllowance;

public class EmployeeAllowanceMapper {

    public static EmployeeAllowanceResponseDTO toDto(EmployeeAllowance employeeAllowance) {

        EmployeeAllowanceResponseDTO dto = new EmployeeAllowanceResponseDTO();
        dto.setEmpId(employeeAllowance.getEmployee().getId());
        dto.setAllowanceId(employeeAllowance.getAllowanceType().getAllowanceId());
        dto.setEmpCode(employeeAllowance.getEmployee().getEmpCode());
        dto.setAllowanceName(employeeAllowance.getAllowanceType().getName());
        dto.setAllowanceType(employeeAllowance.getAllowanceType().getType().name());
        dto.setAllowanceAmount(employeeAllowance.getAllowanceType().getAmount());
        return dto;
    }

}
