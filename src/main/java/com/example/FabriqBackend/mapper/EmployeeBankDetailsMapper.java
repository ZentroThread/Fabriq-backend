package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.EmployeeBankDetailsDTO;
import com.example.FabriqBackend.model.salary.EmployeeBankDetails;

public class EmployeeBankDetailsMapper {

    public static EmployeeBankDetailsDTO toDto(EmployeeBankDetails entity) {

        if (entity == null) {
            return null;
        }

        EmployeeBankDetailsDTO dto = new EmployeeBankDetailsDTO();

        dto.setId(entity.getId());
        dto.setBankName(entity.getBankName());
        dto.setBranchName(entity.getBranchName());
        dto.setAccountNumber(entity.getAccountNumber());
        dto.setAccountHolderName(entity.getAccountHolderName());

        return dto;
    }

    public static EmployeeBankDetails toEntity(EmployeeBankDetailsDTO dto,EmployeeBankDetails entity) {
        entity.setBankName(dto.getBankName());
        entity.setBranchName(dto.getBranchName());
        entity.setAccountNumber(dto.getAccountNumber());
        entity.setAccountHolderName(dto.getAccountHolderName());
        return entity;
    }
}
