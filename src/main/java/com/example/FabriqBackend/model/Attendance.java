package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class Attendance extends TenantAwareEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "emp_code",nullable = false)
    private String empCode;
    private LocalDate date;
    private LocalTime time;
    private String status;  //IN or Out
}
//1, test, 2025-11-01, 09:05, IN
//1, test, 2025-11-01, 17:33, OUT
