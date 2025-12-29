package com.example.FabriqBackend.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.Data;

import java.util.List;

@Data
public class CreateBillingWithRentalsDto {
    private String customerCode;
    private List<AttireRentItemDto> items;
}
