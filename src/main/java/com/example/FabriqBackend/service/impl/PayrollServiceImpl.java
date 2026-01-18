package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.PayrollRecordDao;
import com.example.FabriqBackend.dto.salary.EpfFormDTO;
import com.example.FabriqBackend.dto.salary.EtfFormDTO;
import com.example.FabriqBackend.dto.salary.PayrollRecordResponseDTO;
import com.example.FabriqBackend.mapper.PayrollMapper;
import com.example.FabriqBackend.model.salary.PayrollRecord;
import com.example.FabriqBackend.service.IPayrollService;
import com.example.FabriqBackend.service.payroll.EpfReportService;
import com.example.FabriqBackend.service.payroll.EtfReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements IPayrollService {

    private final PayrollRecordDao payrollRecordDao;
    private final EpfReportService epfReportService;
    private final EtfReportService etfReportService;

    @Override
    public List<PayrollRecordResponseDTO> getPayrollRecordsByEmployeeIdAndYear(Long empId, Integer year) {
        List<PayrollRecord> payrollRecords = payrollRecordDao.findByEmployee_IdAndYear(empId, year);
        List<PayrollRecordResponseDTO> responseDTOs =  payrollRecords.stream()
                .map(PayrollMapper::toRecordResponseDTO)
                .toList();
        if(!responseDTOs.isEmpty()){
            return responseDTOs;
        }
        return List.of();
    }

    @Override
    public List<EpfFormDTO> getEpfFormData(Integer month, Integer year) {
        List<EpfFormDTO> epfData = epfReportService.getEpfForm(month, year);
        if(!epfData.isEmpty()){
            return epfData;
        }
        return List.of();
    }

    @Override
    public List<EtfFormDTO> getEtfFormData(Integer month, Integer year) {
        List<EtfFormDTO> etfData = etfReportService.getEtfForm(month, year);
        if(!etfData.isEmpty()){
            return etfData;
        }
        return List.of();
    }
}
