package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.MeasurementAddDto;
import com.example.FabriqBackend.dto.MeasurementUpdateDto;
import org.springframework.http.ResponseEntity;

public interface IMeasurementService {
    ResponseEntity<?> createMeasurements(MeasurementAddDto dto);
    ResponseEntity<?> getAllMeasurements();
    ResponseEntity<?> deleteMeasurement(Integer id);
    ResponseEntity<?> updateMeasurement(Integer id, MeasurementUpdateDto measurementUpdateDto);
    ResponseEntity<?> getMeasurementById(Integer id);
}
