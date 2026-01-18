package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.EpfFormDTO;
import com.example.FabriqBackend.dto.salary.EtfFormDTO;
import com.example.FabriqBackend.dto.salary.PayrollRecordResponseDTO;

import java.util.List;

public interface IPayrollService {

    List<PayrollRecordResponseDTO> getPayrollRecordsByEmployeeIdAndYear(Long empId, Integer year);

    List<EpfFormDTO> getEpfFormData(Integer month, Integer year);

    List<EtfFormDTO> getEtfFormData(Integer month, Integer year);
}
