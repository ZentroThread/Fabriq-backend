package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.AdvancePayment;

import java.time.LocalDate;
import java.util.List;

public interface AdvancePaymentDao  extends TenantAwareDao<AdvancePayment, Long> {

    List<AdvancePayment> findByEmployeeId(Long empId);

    List<AdvancePayment> findByEmployeeIdAndDateBetween(Long employeeId, LocalDate dateAfter, LocalDate dateBefore);

}