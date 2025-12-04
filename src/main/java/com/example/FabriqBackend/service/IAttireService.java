package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.AttireUpdateDto;
import com.example.FabriqBackend.model.Attire;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IAttireService {
    ResponseEntity<?> createAttire(Attire attire, MultipartFile image);
    ResponseEntity<?> deleteAttire(Integer id);
    ResponseEntity<?> updateAttire(Integer id, AttireUpdateDto attireUpdateDto);
    ResponseEntity<?> getAttireById(Integer id);
    ResponseEntity<?> getAttireByAttireCode(String attireCode);
    List<Attire> getAttireByStatus(String status);
    List<Attire> getAttireByCategoryId(Integer categoryId);
    List<Attire> getAllAttire();
}
