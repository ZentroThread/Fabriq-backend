package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.ProductionRecordDao;
import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;
import com.example.FabriqBackend.mapper.ProductionRecordMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.ProductionRecord;
import com.example.FabriqBackend.service.IProductionRecordService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionRecordServiceImpl implements IProductionRecordService {

    private final ProductionRecordDao productionRecordDao;
    private final EmployeeDao employeeDao;

    @Override
    // Create Production Record
    public ProductionRecordRequestDTO createProductionRecord(ProductionRecordRequestDTO productionRecordRequestDTO) {

        Employee employee = employeeDao.findById(productionRecordRequestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + productionRecordRequestDTO.getEmpId()));

        ProductionRecord productionRecord = ProductionRecordMapper.toEntity(productionRecordRequestDTO);
        productionRecord.setEmployee(employee);

        productionRecordDao.save(productionRecord);

        return productionRecordRequestDTO;
    }

    @Override
    // Get Production Records by Employee ID
    public List<ProductionRecordResponseDTO> getRecordsByEmployeeId(Long employeeId) {

        List<ProductionRecord> records = productionRecordDao.findByEmployee_Id(employeeId)
                .orElseThrow(() -> new RuntimeException("No production records found for employee id: " + employeeId));

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    // Get Production Records by Date Range
    public List<ProductionRecordResponseDTO> getRecordsByDateRange(String startDate, String endDate) {

        List<ProductionRecord> records = productionRecordDao.findByDateBetween(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate))
                .orElseThrow(() -> new RuntimeException("No production records found between dates: " + startDate + " and " + endDate));

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    // Get All Production Records
    public List<ProductionRecordResponseDTO> getAllProductionRecords() {

        List<ProductionRecord> records = productionRecordDao.findAll();

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    // Get Production Records by Date Range and Employee ID
    public List<ProductionRecordResponseDTO> getRecordsByDataRangeAndEmpId(String startDate, String endDate, Long empId) {

        List<ProductionRecord> records = productionRecordDao.findByDateBetweenAndEmployee_Id(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate),
                        empId)
                .orElseThrow(() -> new RuntimeException("No production records found between dates: " + startDate + " and " + endDate + " for employee id: " + empId));

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    public void deleteProductionRecordById(Long recordId) {

        ProductionRecord record = productionRecordDao.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Production record not found with id: " + recordId));

        productionRecordDao.deleteById(recordId);
    }

    @Override
    public ProductionRecordResponseDTO updateProductionRecord(Long recordId, ProductionRecordRequestDTO productionRecordRequestDTO) {

        ProductionRecord existingRecord = productionRecordDao.findById(recordId)
                .orElseThrow(() -> new RuntimeException("Production record not found with id: " + recordId));
        ProductionRecord updatedRecord = ProductionRecordMapper.toEntity(productionRecordRequestDTO);
        updatedRecord.setId(existingRecord.getId());
        Employee employee = employeeDao.findById(productionRecordRequestDTO.getEmpId())
                .orElseThrow(() -> new RuntimeException("Employee not found with id: " + productionRecordRequestDTO.getEmpId()));
        updatedRecord.setEmployee(employee);
        productionRecordDao.save(updatedRecord);

        return ProductionRecordMapper.toDTO(updatedRecord);
    }

}
