package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttireRentAddDto;
import com.example.FabriqBackend.service.IAttireRentService;
import com.example.FabriqBackend.service.impl.AttireRentServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/v1/attire-rent")// Base URL for attire rent-related operations
@RequiredArgsConstructor
public class AttireRentController {

    private final IAttireRentService attireRentService;

    @PostMapping("/add")
    public ResponseEntity<?> addAttireRent(@RequestBody AttireRentAddDto dto) {
        return attireRentService.addAttireRent(dto);
    }

    @GetMapping("/all")
    public ResponseEntity<?> getAllAttireRent() {
        return attireRentService.getAllAttireRent();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAttireRent(@PathVariable Integer id) {
        return attireRentService.deleteAttireRent(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAttireRent(@PathVariable Integer id, @RequestBody AttireRentAddDto dto) {
        return attireRentService.updateAttireRent(id, dto);
    }
}
