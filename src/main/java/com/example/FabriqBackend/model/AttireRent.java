package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "attire_rent")
public class AttireRent extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    private LocalDateTime rentDate;
    private LocalDateTime returnDate;
    private Integer rentDuration; // number of days

    @Column(name = "attire_code")  // ← Add this
    private String attireCode;

    @Column(name = "cust_code")    // ← Add this
    private String custCode;

    @Column(name = "billing_code") // ← Add this
    private String billingCode;

    @ManyToOne
    @JoinColumn(name = "attire_id", referencedColumnName = "id")
    private Attire attire;

    @ManyToOne
    @JoinColumn(name = "cust_id", referencedColumnName = "cust_id")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "billing_id", referencedColumnName = "billing_id")
    private Billing billing;
}

