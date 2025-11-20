package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "customer")

public class Customer extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cust_id")
    private Integer custId;

    @Column(name = "cust_code", unique = true)
    private String custCode;

    private String custName;
    private String custEmail;
    private String custAddress;
    private String custHomePhoneNumber;
    private String custMobileNumber;
    private String custWhatsappNumber;

    @PostPersist
    public void generateId() {
        this.custCode = String.format("CUS-%04d", this.custId);
    }

}
