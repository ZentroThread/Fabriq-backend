package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.PayrollRecord;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PayrollRecordDao extends TenantAwareDao<PayrollRecord, Long> {

    List<PayrollRecord> findByEmployee_IdAndYear(Long employeeId, Integer year);

    List<PayrollRecord> findByMonthAndYearAndConfirmedTrue(Integer month, Integer year);

}
