package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.EmployeeDao;
import com.example.FabriqBackend.dao.ProductionRecordDao;
import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;
import com.example.FabriqBackend.mapper.ProductionRecordMapper;
import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.salary.ProductionRecord;
import com.example.FabriqBackend.service.Interface.IProductionRecordService;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@CacheConfig(cacheNames = "productionRecords")
public class ProductionRecordServiceImpl implements IProductionRecordService {

    private final ProductionRecordDao productionRecordDao;
    private final EmployeeDao employeeDao;

    @Override
    @CacheEvict(
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public ProductionRecordRequestDTO createProductionRecord(ProductionRecordRequestDTO productionRecordRequestDTO) {

        Employee employee = employeeDao.findById(productionRecordRequestDTO.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(productionRecordRequestDTO.getEmpId())));

        ProductionRecord productionRecord = ProductionRecordMapper.toEntity(productionRecordRequestDTO);
        productionRecord.setEmployee(employee);

        productionRecordDao.save(productionRecord);

        return productionRecordRequestDTO;
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':empId:' + #employeeId")
    public List<ProductionRecordResponseDTO> getRecordsByEmployeeId(Long employeeId) {

        List<ProductionRecord> records = productionRecordDao.findByEmployee_Id(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", "employeeId", String.valueOf(employeeId)));

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':dates:' + #startDate + ':' + #endDate")
    public List<ProductionRecordResponseDTO> getRecordsByDateRange(String startDate, String endDate) {

        List<ProductionRecord> records = productionRecordDao.findByDateBetween(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate))
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", "dateRange", startDate + " to " + endDate));

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':allProductionRecords'")
    public List<ProductionRecordResponseDTO> getAllProductionRecords() {

        List<ProductionRecord> records = productionRecordDao.findAll();

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':empId:' + #empId + ':dates:' + #startDate + ':' + #endDate")
    public List<ProductionRecordResponseDTO> getRecordsByDataRangeAndEmpId(String startDate, String endDate, Long empId) {

        List<ProductionRecord> records = productionRecordDao.findByDateBetweenAndEmployee_Id(
                        LocalDate.parse(startDate),
                        LocalDate.parse(endDate),
                        empId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", "empId and dates", empId + " " + startDate + " to " + endDate));

        return records.stream()
                .map(ProductionRecordMapper::toDTO)
                .toList();
    }

    @Override
    @CacheEvict(
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public void deleteProductionRecordById(Long recordId) {

        ProductionRecord record = productionRecordDao.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", "id", String.valueOf(recordId)));

        productionRecordDao.deleteById(recordId);
    }

    @Override
    @CacheEvict(
        key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + '*'"
    )
    public ProductionRecordResponseDTO updateProductionRecord(Long recordId, ProductionRecordRequestDTO productionRecordRequestDTO) {

        ProductionRecord existingRecord = productionRecordDao.findById(recordId)
                .orElseThrow(() -> new ResourceNotFoundException("ProductionRecord", "id", String.valueOf(recordId)));
        ProductionRecord updatedRecord = ProductionRecordMapper.toEntity(productionRecordRequestDTO);
        updatedRecord.setId(existingRecord.getId());
        Employee employee = employeeDao.findById(productionRecordRequestDTO.getEmpId())
                .orElseThrow(() -> new ResourceNotFoundException("Employee", "id", String.valueOf(productionRecordRequestDTO.getEmpId())));
        updatedRecord.setEmployee(employee);
        productionRecordDao.save(updatedRecord);

        return ProductionRecordMapper.toDTO(updatedRecord);
    }

}
