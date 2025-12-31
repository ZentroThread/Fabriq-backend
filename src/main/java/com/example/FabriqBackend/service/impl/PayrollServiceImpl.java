package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.PayrollRecordDao;
import com.example.FabriqBackend.dto.salary.PayrollRecordResponseDTO;
import com.example.FabriqBackend.mapper.PayrollMapper;
import com.example.FabriqBackend.model.salary.PayrollRecord;
import com.example.FabriqBackend.service.IPayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements IPayrollService {

    private final PayrollRecordDao payrollRecordDao;
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
}
