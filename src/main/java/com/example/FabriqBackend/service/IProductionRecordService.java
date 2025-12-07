package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;

import java.util.List;

public interface IProductionRecordService {

    ProductionRecordRequestDTO createProductionRecord(ProductionRecordRequestDTO productionRecordRequestDTO);
    List<ProductionRecordResponseDTO> getRecordsByEmployeeId(Long employeeId);
    List<ProductionRecordResponseDTO> getRecordsByDateRange(String startDate, String endDate);
    List<ProductionRecordResponseDTO> getAllProductionRecords();
    List<ProductionRecordResponseDTO> getRecordsByDataRangeAndEmpId(String startDate, String endDate, Long empId);
    void deleteProductionRecordById(Long recordId);
    ProductionRecordResponseDTO updateProductionRecord(Long recordId, ProductionRecordRequestDTO productionRecordRequestDTO);
}
