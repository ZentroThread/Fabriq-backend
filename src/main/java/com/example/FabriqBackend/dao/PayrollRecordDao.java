package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.PayrollRecord;

import java.util.List;

public interface PayrollRecordDao extends TenantAwareDao<PayrollRecord, Long> {

    List<PayrollRecord> findByEmployee_IdAndYear(Long employeeId, Integer year);

    List<PayrollRecord> findByMonthAndYearAndConfirmedTrue(Integer month, Integer year);

}
