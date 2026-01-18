package com.example.FabriqBackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AttireRentItemDto {

    private String attireCode;
    // Frontend sends plain dates (yyyy-MM-dd) — use LocalDate to match that
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate rentDate;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate returnDate;
    
    // Fields for customized items
    private Boolean isCustomItem;
    private String customItemName;
    private Double customPrice;
}
