package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "billing")
public class Billing  extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "billing_id")
    private Integer billingId;
    @Column(name = "billing_code", unique = true)
    private String billingCode;
    private String billingTotal;
    private String billingStatus;
    private String billingType;

    @CreationTimestamp
    @Column(name = "billing_date", nullable = false, updatable = false)
    private LocalDateTime billingDate;

    @ManyToOne
    @JoinColumn(name = "cust_id", referencedColumnName = "cust_id")
    private Customer customer;

    @PrePersist
    public void generateCode() {
        if (this.billingCode == null) {
            // Generate random unique code: BIL-20251229-XXXX
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            String random = String.format("%04d", (int)(Math.random() * 10000));
            this.billingCode = "BIL-" + timestamp + "-" + random;
        }
    }

}
