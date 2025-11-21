package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.AttireRentAddDto;
import org.springframework.http.ResponseEntity;

public interface IAttireRentService {
    ResponseEntity<?> addAttireRent(AttireRentAddDto attireRentAddDto);
    ResponseEntity<?> getAllAttireRent();
    ResponseEntity<?> deleteAttireRent(Integer id);
    ResponseEntity<?> updateAttireRent(Integer id, AttireRentAddDto dto);
}
