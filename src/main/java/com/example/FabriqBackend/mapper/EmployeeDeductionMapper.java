package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.EmployeeDeductionResponseDTO;

public class EmployeeDeductionMapper {

    public static EmployeeDeductionResponseDTO toDto(com.example.FabriqBackend.model.salary.EmployeeDeduction employeeDeduction) {

        EmployeeDeductionResponseDTO dto = new EmployeeDeductionResponseDTO();

        dto.setEmpId(employeeDeduction.getEmployee().getId());
        dto.setDeductionId(employeeDeduction.getDeductionType().getDeductionId());
        dto.setEmpCode(employeeDeduction.getEmployee().getEmpCode());
        dto.setDeductionName(employeeDeduction.getDeductionType().getName());
        dto.setDeductionType(employeeDeduction.getDeductionType().getType().name());
        dto.setDeductionAmount(employeeDeduction.getDeductionType().getAmount());

        return dto;
    }
}
