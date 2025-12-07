package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.ProductionRecord;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface ProductionRecordDao extends TenantAwareDao<ProductionRecord, Long> {


    Optional<List<ProductionRecord>> findByEmployee_Id(Long employeeId);
    Optional<List<ProductionRecord>> findByDateBetween(LocalDate startDate, LocalDate endDate);
    Optional<List<ProductionRecord>> findByDateBetweenAndEmployee_Id(LocalDate startDate, LocalDate endDate, Long empId);

}
