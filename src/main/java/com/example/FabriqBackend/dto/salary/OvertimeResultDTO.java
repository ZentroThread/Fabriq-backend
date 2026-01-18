package com.example.FabriqBackend.dto.salary;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class OvertimeResultDTO {

    double singleHours;
    double doubleHours;
    double singleAmount;
    double doubleAmount;
    double singleOTHourlyRate;
    double doubleOTHourlyRate;

    public double total() {
        return singleAmount + doubleAmount;
    }

}
