package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttireAvailableResponseDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.service.IAttireRentService;
import com.example.FabriqBackend.service.IAttireService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/public/{tenant}/attire")
@RequiredArgsConstructor
public class PublicAttireController {

    private final IAttireService attireService;
    private final IAttireRentService attireRentService;

    @GetMapping
    @Operation(
            summary = "Get all attire (public)",
            description = "Returns all attire for a specific tenant (public)"
    )
    public List<Attire> getAllPublic(@PathVariable String tenant) {
        return attireService.getAllAttire();
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get attire by ID (public)",
            description = "Returns a single attire item (public)"
    )
    public ResponseEntity<?> getByIdPublic(
            @PathVariable String tenant,
            @PathVariable Integer id
    ) {
        return ResponseEntity.ok(attireService.getAttireById(id));
    }

    @GetMapping("/{attireCode}/availability")
    public ResponseEntity<AttireAvailableResponseDto> checkAvailability(
            @PathVariable String attireCode,
            @RequestParam("rentDate")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime rentDate
    ) {
        return ResponseEntity.ok(
                attireRentService.checkAvailability(attireCode, rentDate)
        );
    }

}

