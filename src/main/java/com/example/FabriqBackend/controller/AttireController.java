package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.service.IAttireService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/attire")
@RequiredArgsConstructor
public class AttireController {

    private final IAttireService attireService;
    private final ObjectMapper objectMapper;

    @PostMapping("/add")
    @Operation(
            summary = "Create a new attire",
            description = "Creates a new attire record with details such as attire code, name, category, and status."
    )
    public ResponseEntity<?> createAttire(@RequestParam("attire") String attireJson, @RequestParam(value = "image", required = false) MultipartFile image) {
        try {
            Attire attire = objectMapper.readValue(attireJson, Attire.class);
            return attireService.createAttire(attire, image);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid attire data: " + e.getMessage());
        }
    }

    @GetMapping("/all")
    @Operation(
            summary = "Get all attire records",
            description = "Returns a list of all attire items currently available in the system."
    )
    public List<Attire> getAllAttire() {
        return attireService.getAllAttire();
    }

    @DeleteMapping("/delete/{id}")
    @Operation(
            summary = "Delete attire by ID",
            description = "Deletes the attire item that matches the given ID. If the ID is not found, a not-found response is returned."
    )
    public ResponseEntity<?> deleteAttire(@PathVariable Integer id) {
        return attireService.deleteAttire(id);
    }

    @PutMapping("/update/{id}")
    @Operation(
            summary = "Update attire details",
            description = "Updates specific fields of an attire record using the provided AttireUpdateDto. Only fields included in the DTO will be modified."
    )
    public ResponseEntity<?> updateAttire(@PathVariable Integer id, @RequestBody AttireUpdateDto attireUpdateDto) {
        return attireService.updateAttire(id, attireUpdateDto);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get attire by ID",
            description = "Fetches the attire details for the given attire ID."
    )
    public ResponseEntity<?> getAttireById(@PathVariable Integer id) {
        return attireService.getAttireById(id);
    }

    @GetMapping("/code/{attireCode}")
    @Operation(
            summary = "Get attire by attire code",
            description = "Retrieves a single attire item using its unique attire code."
    )
    public ResponseEntity<?> getAttireByAttireCode(@PathVariable String attireCode) {
        return attireService.getAttireByAttireCode(attireCode);
    }

    @GetMapping("/status/{status}")
    @Operation(
            summary = "Filter attire by status",
            description = "Returns all attire items matching the specified status, such as AVAILABLE, DAMAGED, or ASSIGNED."
    )
    public List<Attire> getAttireByStatus(@PathVariable String status) {
        return attireService.getAttireByStatus(status);
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
            summary = "Filter attire by category",
            description = "Fetches all attire items that belong to the given category ID."
    )
    public List<Attire> getAttireByCategoryId(@PathVariable Integer categoryId) {
        return attireService.getAttireByCategoryId(categoryId);
    }
}
