package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dao.MeasurementDao;
import com.example.FabriqBackend.dto.MeasurementAddDto;
import com.example.FabriqBackend.dto.MeasurementUpdateDto;
import com.example.FabriqBackend.model.Measurement;
import com.example.FabriqBackend.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/measurement")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;


    @PostMapping("/add")
    public ResponseEntity<?> createMeasurements(@RequestBody MeasurementAddDto dto) {
        return measurementService.createMeasurements(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllMeasurements() {
        return measurementService.getAllMeasurements();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteMeasurement(@PathVariable Integer id) {
        return measurementService.deleteMeasurement(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateMeasurement(@PathVariable Integer id, @RequestBody MeasurementUpdateDto measurementDao) {
        return measurementService.updateMeasurement(id,measurementDao );
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getMeasurementById(@PathVariable Integer id) {
        return measurementService.getMeasurementById(id);
    }
}
