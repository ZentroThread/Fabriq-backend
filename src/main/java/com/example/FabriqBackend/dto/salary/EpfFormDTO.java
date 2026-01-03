package com.example.FabriqBackend.dto.salary;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class EpfFormDTO {

    private String employeeName;
    private String nic;
    private String epfNumber;

    private Double epfSalary;
    private Double epfEmployeeContribution;
    private Double epfEmployerContribution;
    private Double total;
}
