package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttireCreateDto;
import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.dto.ReservationRequest;
import com.example.FabriqBackend.dto.StockUpdate;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.service.IAttireService;
import com.example.FabriqBackend.service.StockService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/v1/attire")
@RequiredArgsConstructor
public class AttireController {

    private final IAttireService attireService;
    private final StockService stockService;


    @PostMapping(value = "/add", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Create a new attire", description = "Create a new attire item with optional image upload and details provided in AttireCreateDto")
    public ResponseEntity<?> createAttire(
            @ModelAttribute AttireCreateDto dto,
            @RequestParam(value = "image", required = false) MultipartFile image) {

        return attireService.createAttire(dto, image);
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

    @PutMapping(value = "/update/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Update attire details",
            description = "Updates specific fields of an attire record using the provided AttireUpdateDto. Only fields included in the DTO will be modified."
    )
    public ResponseEntity<?> updateAttire(@PathVariable Integer id,  @ModelAttribute AttireUpdateDto attireUpdateDto, @RequestParam(value = "image", required = false) MultipartFile image) {
        return attireService.updateAttire(id, attireUpdateDto , image);
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

//    @PostMapping("/reserve")
//    public ResponseEntity<?> reserveItem(@RequestBody ReservationRequest req) {
//        stockService.reserveItem(req.getAttireCode(), req.getCustomerCode());
//        return ResponseEntity.ok("Reserved");
//    }

    @PostMapping("/reserve")
    @Operation(summary = "Reserve an attire item", description = "Reserve an attire item for a customer and return updated stock information")
    public ResponseEntity<?> reserveItem(@RequestBody ReservationRequest req) {
        try {
            StockUpdate update = stockService.reserveItem(
                    req.getAttireCode(),
                    req.getCustomerCode()
            );
            return ResponseEntity.ok(update);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unreserve")
    @Operation(summary = "Unreserve an attire item", description = "Release a previously reserved attire item for a customer and return updated stock info")
    public ResponseEntity<?> unreserveItem(@RequestBody ReservationRequest req) {
        try {
            StockUpdate update = stockService.unreserveItem(
                    req.getAttireCode(),
                    req.getCustomerCode()
            );
            return ResponseEntity.ok(update);
        } catch (IllegalArgumentException | IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
