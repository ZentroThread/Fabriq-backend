package com.example.FabriqBackend.dto;

import lombok.Data;

@Data
public class MeasurementAddDto {

    private Double shoulderWidth;
    private Double bust;
    private Double waist;
    private Double hip;
    private Double sleeveLength;

    private String custCode;
    private String attireCode;
    private String categoryCode;
}
