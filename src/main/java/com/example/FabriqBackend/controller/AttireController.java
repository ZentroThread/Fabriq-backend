package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.model.Attire;
import com.example.FabriqBackend.service.AttireService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/attire")
@RequiredArgsConstructor
public class AttireController {

    private final AttireService attireService;

    @PostMapping("/add")
    public ResponseEntity<?> createAttire(@RequestBody Attire attire) {
        return attireService.createAttire(attire);
    }

    @GetMapping("/all")
    public List<Attire> getAllAttire() {
        return attireService.getAllAttire();
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteAttire(@PathVariable Integer id) {
        return attireService.deleteAttire(id);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateAttire(@PathVariable Integer id, @RequestBody AttireUpdateDto attireUpdateDto) {
        return attireService.updateAttire(id, attireUpdateDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getAttireById(@PathVariable Integer id) {
        return attireService.getAttireById(id);
    }

    @GetMapping("/code/{attireCode}")
    public ResponseEntity<?> getAttireByAttireCode(@PathVariable String attireCode) {
        return attireService.getAttireByAttireCode(attireCode);
    }

    @GetMapping("/status/{status}")
    public List<Attire> getAttireByStatus(@PathVariable String status) {
        return attireService.getAttireByStatus(status);
    }

    @GetMapping("/category/{categoryId}")
    public List<Attire> getAttireByCategoryId(@PathVariable Integer categoryId) {
        return attireService.getAttireByCategoryId(categoryId);
    }
}
