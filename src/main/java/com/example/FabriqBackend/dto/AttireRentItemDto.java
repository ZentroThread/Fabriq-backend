package com.example.FabriqBackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttireRentItemDto {

    private String attireCode;
    private LocalDateTime rentDate;      // ← Keep LocalDateTime but accept alias mapping on parent DTO
    private LocalDateTime returnDate;
}
