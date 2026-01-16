package com.example.FabriqBackend.service.payroll;

import com.example.FabriqBackend.dao.ProductionRecordDao;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.ProductionRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionPayService {

    private final ProductionRecordDao productionRecordDao;

    public double calculate(Employee employee, YearMonth period){
        LocalDate startDate = period.atDay(1);
        LocalDate endDate = period.atEndOfMonth();

        List<ProductionRecord> productionRecords =
                productionRecordDao.findByEmployee_IdAndDateBetween(employee.getId(), startDate, endDate)
                        .orElse(List.of());

        return productionRecords.isEmpty()
                ? 0.0
                : productionRecords.stream()
                .mapToDouble(pr -> pr.getQuantity() * pr.getRatePerProduct())
                .sum();
    }
}
