package com.example.FabriqBackend.dto;

import lombok.Data;

@Data
public class AttireUpdateDto {

    private String attireCode;
    private String attireName;
    private Double attirePrice;
    private String attireStatus;
}
