package com.example.FabriqBackend.dto.salary;

import lombok.Data;

@Data
public class EmployeeBankDetailsDTO {

    private Long id;
    private String bankName;
    private String branchName;
    private String accountNumber;
    private String accountHolderName;

}
