package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.PayrollRecordResponseDTO;

import java.util.List;

public interface IPayrollService {

    List<PayrollRecordResponseDTO> getPayrollRecordsByEmployeeIdAndYear(Long empId, Integer year);
}
