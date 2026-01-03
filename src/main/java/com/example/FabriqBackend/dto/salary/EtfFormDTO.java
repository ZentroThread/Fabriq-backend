package com.example.FabriqBackend.dto.salary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter @AllArgsConstructor
public class EtfFormDTO {

    private String employeeName;
    private String nic;
    private String epfNumber;
    private Double etfSalary;
    private Double etfContribution;
}
