package com.example.FabriqBackend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttireCreateDto {
    private String attireCode;
    private String attireName;
    private String attireDescription;
    private Double attirePrice;
    private String attireStatus;
    private Integer categoryId;
    private Integer attireStock;

}