package com.example.FabriqBackend.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
@Table(name = "attire_rent")
public class AttireRent extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime rentDate;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
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
    
    // Fields for customized items (when attire is null)
    @Column(name = "is_custom_item")
    private Boolean isCustomItem;
    
    @Column(name = "custom_item_name")
    private String customItemName;
    
    @Column(name = "custom_price")
    private Double customPrice;
}

