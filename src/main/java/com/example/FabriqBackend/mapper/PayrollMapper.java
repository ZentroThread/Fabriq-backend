package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.PayrollRecordResponseDTO;
import com.example.FabriqBackend.model.salary.PayrollRecord;

public class PayrollMapper {

    public static PayrollRecordResponseDTO toRecordResponseDTO(PayrollRecord payrollRecord) {
        PayrollRecordResponseDTO dto = new PayrollRecordResponseDTO();
        dto.setMonth(payrollRecord.getMonth());
        dto.setYear(payrollRecord.getYear());
        dto.setNetSalary(payrollRecord.getNetSalary());
        dto.setConfirmed(payrollRecord.isConfirmed());
        return dto;
    }
}
