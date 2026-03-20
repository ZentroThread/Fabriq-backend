package com.example.FabriqBackend.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class BookingRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tenantId;
    private Long attireId;

    private LocalDate startDate;
    private LocalDate endDate;

    private String status = "PENDING"; // PENDING, APPROVED, REJECTED
    private String customerName;
    private String userEmail;

}
