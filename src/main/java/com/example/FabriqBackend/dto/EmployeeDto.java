package com.example.FabriqBackend.dto;


import lombok.*;
@Data
public class EmployeeDto {
    private Long empId;
    private String empFirstName;
    private String empLastName;
    private String nicNumber;
    private String mobileNumber;
    private String dateOfBirth;
    private String role;
    private String address;
    //age - computed field
    private Integer age;
}
