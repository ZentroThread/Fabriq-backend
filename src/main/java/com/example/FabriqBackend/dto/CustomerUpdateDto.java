package com.example.FabriqBackend.dto;

import lombok.Data;

@Data
public class CustomerUpdateDto {

    private String custName;
    private String custEmail;
    private String custAddress;
    private String custHomePhoneNumber;
    private String custMobileNumber;
    private String custWhatsappNumber;
}
