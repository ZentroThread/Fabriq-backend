package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class PayrollSaveRequestDTO {
    private Integer empId;
    private Integer month;
    private Integer year;

}
