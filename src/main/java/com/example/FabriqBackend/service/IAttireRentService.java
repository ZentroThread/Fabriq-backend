package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.AttireRentAddDto;
import com.example.FabriqBackend.dto.AttireRentDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface IAttireRentService {
    ResponseEntity<?> addAttireRent(AttireRentAddDto attireRentAddDto);
    List<AttireRentDto> getAllAttireRent();
    ResponseEntity<?> getAttireRentsByBillingCode(String billingCode);
    ResponseEntity<?> deleteAttireRent(Integer id);
    ResponseEntity<?> updateAttireRent(Integer id, AttireRentAddDto dto);
}
