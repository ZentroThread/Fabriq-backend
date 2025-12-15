package com.example.FabriqBackend.dto;

import lombok.Data;

@Data
public class AttireUpdateDto {

    private String attireCode;
    private String attireName;
    private String attireDescription;
    private Double attirePrice;
    private String attireStatus;
    private Integer categoryId;
    private Integer attireStock;
}
