package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
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
    private Date rentDate;
    private Date returnDate;
    private Date rentDuration;

    @ManyToOne
    @JoinColumn(name = "attire_code", referencedColumnName = "id")
    private Attire attire;

    @ManyToOne
    @JoinColumn(name = "cust_code", referencedColumnName = "cust_id")
    private Customer customer;
}
