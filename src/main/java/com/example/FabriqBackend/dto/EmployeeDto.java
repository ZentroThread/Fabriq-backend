package com.example.FabriqBackend.dto;


import com.example.FabriqBackend.dto.salary.EmployeeBankDetailsDTO;
import com.example.FabriqBackend.enums.GenderEnum;
import lombok.*;

import java.time.LocalDate;

@Data
public class EmployeeDto {
    private Long id;
    private String empCode;
    private String imgUrl;
    private String empFirstName;
    private String empLastName;
    private String nicNumber;
    private String mobileNumber;
    private String dateOfBirth;
    private String role;
    private String address;
    private GenderEnum gender;
    private String joinedDate;
    private String epfNumber;
    private Double basicSalary;
    private boolean commissionEligible;
    private Integer performancePoints;
    //age - computed field
    private Integer age;

    private EmployeeBankDetailsDTO employeeBankDetails;
}
