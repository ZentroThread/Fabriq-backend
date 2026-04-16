package com.example.FabriqBackend.service.Interface.Ipayroll;

import com.example.FabriqBackend.dto.salary.PayrollResponseDTO;

public interface PayrollService {
    PayrollResponseDTO calculate(Long empId, int month, int year);
    PayrollResponseDTO confirmAndSave(Long empId, int month, int year);
}
