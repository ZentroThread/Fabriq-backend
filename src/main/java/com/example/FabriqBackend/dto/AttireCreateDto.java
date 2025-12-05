package com.example.FabriqBackend.dto;

import lombok.Data;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttireCreateDto {
    private String attireCode;
    private String attireName;
    private Double attirePrice;
    private String attireStatus;
    private Integer categoryId;
}