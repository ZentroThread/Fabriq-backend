package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.MeasurementAddDto;
import com.example.FabriqBackend.dto.MeasurementUpdateDto;
import com.example.FabriqBackend.service.IMeasurementService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/measurement")
@RequiredArgsConstructor
public class MeasurementController {

    private final IMeasurementService measurementService;


    @PostMapping("/add")
    @Operation(
            summary = "Create a new measurement record",
            description = "Adds a new set of customer body measurements."
    )
    public ResponseEntity<?> createMeasurements(@RequestBody MeasurementAddDto dto) {
        return measurementService.createMeasurements(dto);
    }

    @GetMapping("/all")
    @Operation(
            summary = "Retrieve all measurement records",
            description = "Returns a complete list of all saved customer measurement entries."
    )
    public ResponseEntity<?> getAllMeasurements() {
        return measurementService.getAllMeasurements();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete a measurement record by ID",
            description = "Removes an existing measurement entry using its ID. "
                    + "Returns an error if the record does not exist."
    )
    public ResponseEntity<?> deleteMeasurement(@PathVariable Integer id) {
        return measurementService.deleteMeasurement(id);
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update an existing measurement record",
            description = "Updates measurement details for a given ID using the provided MeasurementUpdateDto. "
                    + "Only the fields sent in the request will be updated."
    )
    public ResponseEntity<?> updateMeasurement(@PathVariable Integer id, @RequestBody MeasurementUpdateDto measurementDao) {
        return measurementService.updateMeasurement(id, measurementDao);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get a measurement record by ID",
            description = "Fetches a single measurement entry based on the provided ID."
    )
    public ResponseEntity<?> getMeasurementById(@PathVariable Integer id) {
        return measurementService.getMeasurementById(id);
    }
}
