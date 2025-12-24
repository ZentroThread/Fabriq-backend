package com.example.FabriqBackend.mapper;

import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;
import com.example.FabriqBackend.model.salary.ProductionRecord;

public class ProductionRecordMapper {

    public static ProductionRecordResponseDTO toDTO(ProductionRecord productionRecord) {

        ProductionRecordResponseDTO dto = new ProductionRecordResponseDTO();

        dto.setId(productionRecord.getId());
        dto.setDate(productionRecord.getDate().toString());
        dto.setProductionName(productionRecord.getProductionName());
        dto.setQuantity(productionRecord.getQuantity());
        dto.setRatePerProduct(productionRecord.getRatePerProduct());
        dto.setEmpId(productionRecord.getEmployee().getId());
        dto.setEmpCode(productionRecord.getEmployee().getEmpCode());
        dto.setEmpName(productionRecord.getEmployee().getEmpFirstName());

        return dto;
    }

    public static ProductionRecord toEntity(ProductionRecordRequestDTO dto) {

        ProductionRecord productionRecord = new ProductionRecord();

        productionRecord.setDate(dto.getDate());
        productionRecord.setProductionName(dto.getProductionName());
        productionRecord.setQuantity(dto.getQuantity());
        productionRecord.setRatePerProduct(dto.getRatePerProduct());

        return productionRecord;
    }
}
