package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Employee {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false,unique = true)
    private String empCode;
    private String empFirstName;
    private String empLastName;
    private String nicNumber;
    private String mobileNumber;
    private String dateOfBirth;
    private String role;
    private String address;

}
