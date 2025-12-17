package com.example.FabriqBackend.dto.salary;

import com.example.FabriqBackend.enums.AllowanceTypeEnum;
import lombok.Data;

@Data
public class AllowanceTypeDTO {

    private String name;
    private Double amount;
    private AllowanceTypeEnum type;

}
