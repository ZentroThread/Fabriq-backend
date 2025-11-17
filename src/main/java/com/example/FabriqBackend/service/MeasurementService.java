package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.AttireDao;
import com.example.FabriqBackend.dao.CategoryDao;
import com.example.FabriqBackend.dao.CustomerDao;
import com.example.FabriqBackend.dao.MeasurementDao;
import com.example.FabriqBackend.dto.MeasurementAddDto;
import com.example.FabriqBackend.dto.MeasurementUpdateDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.model.Category;
import com.example.FabriqBackend.model.Customer;
import com.example.FabriqBackend.model.Measurement;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "measurements")
public class MeasurementService {

    private final MeasurementDao measurementDao;
    private final ModelMapper modelMapper;
    private final CustomerDao customerDao;
    private final AttireDao attireDao;
    private final CategoryDao categoryDao;

    @CacheEvict(value = "measurements", allEntries = true)
    public ResponseEntity<?> createMeasurements(MeasurementAddDto dto) {

        // Map basic measurement fields using ModelMapper
        Measurement measurement = modelMapper.map(dto, Measurement.class);

        // Fetch and set Customer
        Customer customer = customerDao.findByCustCode(dto.getCustCode()).orElse(null);
        if (customer == null) {
            throw new RuntimeException("Customer not found with id: " + dto.getCustCode());
        }
        measurement.setCustomer(customer);

        // Fetch and set Attire
        Attire attire = attireDao.findByAttireCode(dto.getAttireCode());
        if (attire == null) {
            throw new RuntimeException("Attire not found with code: " + dto.getAttireCode());
        }
        measurement.setAttire(attire);

        // Fetch and set Category
        Category category = categoryDao.findByCategoryCode(dto.getCategoryCode());
        if (category == null) {
            throw new RuntimeException("Category not found with code: " + dto.getCategoryCode());
        }
        measurement.setCategory(category);

        // Save
        measurementDao.save(measurement);
        return ResponseEntity.ok("Measurements created successfully");
    }


    @Cacheable(key ="'allMeasurements'")
    public ResponseEntity<?> getAllMeasurements() {
        List<Measurement> measurements = measurementDao.findAll();
        if (measurements.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(measurements);
    }

    @CacheEvict( key="#id+ ':deletedMeasurement'")
    public ResponseEntity<?> deleteMeasurement(Integer id) {
        Optional<Measurement> measurement = measurementDao.findById(id);
        if (measurement.isPresent()) {
            measurementDao.deleteById(id);
            return ResponseEntity.ok("Measurement deleted successfully");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @CachePut(key = "'updatedMeasurement:' + #id")
    public ResponseEntity<?> updateMeasurement(Integer id, MeasurementUpdateDto measurementUpdateDto) {
        Measurement measurement = measurementDao.findById(id)
                .map( measurement1 -> {
                    modelMapper.map(measurementUpdateDto, measurement1);

                    Measurement updatedMeasurement = measurementDao.save(measurement1);
                    return ResponseEntity.ok().body(updatedMeasurement);
                })
                .orElseGet(() -> ResponseEntity.notFound().build()).getBody();
        return ResponseEntity.ok(measurement);
    }

    @Cacheable(key = "'measurementById:' + #id")
    public ResponseEntity<?> getMeasurementById(Integer id) {
        Measurement measurement = measurementDao.findById(id).orElse(null);
        if (measurement != null) {
            return ResponseEntity.ok(measurement);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
