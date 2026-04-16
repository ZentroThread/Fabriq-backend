package com.example.FabriqBackend.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttireAvailableResponseDto {
    private boolean available;
    private LocalDateTime expectedReturnDate;
    private String message;

    public AttireAvailableResponseDto(
            boolean available,
            LocalDateTime expectedReturnDate,
            String message) {
        this.available = available;
        this.expectedReturnDate = expectedReturnDate;
        this.message = message;
    }
}
