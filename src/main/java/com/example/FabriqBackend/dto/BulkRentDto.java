package com.example.FabriqBackend.dto;

import lombok.Data;

import java.util.List;

@Data
public class BulkRentDto {
    private String customerCode;
    private List<AttireRentItemDto> items;
}
