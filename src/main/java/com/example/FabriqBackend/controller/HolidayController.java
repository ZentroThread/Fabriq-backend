package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.salary.HolidayRequestDTO;
import com.example.FabriqBackend.dto.salary.HolidayResponseDTO;
import com.example.FabriqBackend.service.IHolidayService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/v1/holidays")
@RequiredArgsConstructor
public class HolidayController {

    private final IHolidayService holidayService;

    @PostMapping
    @Operation(
            summary = "Add Holiday",
            description = "This endpoint allows adding a new holiday."
    )
    public ResponseEntity<HolidayResponseDTO> addHoliday(@RequestBody HolidayRequestDTO holidayRequestDTO) {
        HolidayResponseDTO holidayResponseDTO = holidayService.addHoliday(holidayRequestDTO);
        return ResponseEntity.ok(holidayResponseDTO);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get Holiday by ID",
            description = "This endpoint retrieves a holiday by its ID."
    )
    public ResponseEntity<HolidayResponseDTO> getHolidayById(@PathVariable int id) {
        HolidayResponseDTO holidayResponseDTO = holidayService.getHolidayById(id);
        return ResponseEntity.ok(holidayResponseDTO);
    }

    @GetMapping
    @Operation(
            summary = "Get All Holidays",
            description = "This endpoint retrieves all holidays."
    )
    public ResponseEntity<List<HolidayResponseDTO>> getAllHolidays() {
        java.util.List<HolidayResponseDTO> holidays = holidayService.getAllHolidays();
        return ResponseEntity.ok(holidays);
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update Holiday",
            description = "This endpoint allows updating an existing holiday by its ID."
    )
    public ResponseEntity<HolidayResponseDTO> updateHoliday(@PathVariable int id, @RequestBody HolidayRequestDTO holidayRequestDTO) {
        HolidayResponseDTO holidayResponseDTO = holidayService.updateHoliday(id, holidayRequestDTO);
        return ResponseEntity.ok(holidayResponseDTO);
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete Holiday",
            description = "This endpoint allows deleting a holiday by its ID."
    )
    public ResponseEntity<?> deleteHoliday(@PathVariable int id) {
        holidayService.deleteHoliday(id);
        HashMap<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/date-range")
    @Operation(
            summary = "Get Holidays by Date Range",
            description = "This endpoint retrieves holidays within a specified date range."
    )
    public ResponseEntity<List<HolidayResponseDTO>> getHolidaysByDateRange(@RequestParam String startDate, @RequestParam String endDate) {
        List<HolidayResponseDTO> holidays = holidayService.getByDateRange(startDate, endDate);
        return ResponseEntity.ok(holidays);
    }

}
