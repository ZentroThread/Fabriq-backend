package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "tenants")
public class Tenant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @Column(name = "tenantId", nullable = false, unique = true)
    private String tenantId; // e.g., "hiru_sandu_001"

    @Column(name = "name", nullable = false)
    private String name; // Business name: "Hiru Sandu Bridal Wear"

    @Column(name = "branch")
    private String branch; // Branch name if multiple locations

    @Column(name = "email")
    private String email;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "address")
    private String address;

    @Column(name = "city")
    private String city;


    @Column(name = "active", nullable = false)
    private Boolean active = true;



    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;


    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();

    }


}
