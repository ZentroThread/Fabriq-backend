package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "measurement")
public class Measurement extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    private Double shoulderWidth;
    private Double bust;
    private Double waist;
    private Double hip;
    private Double sleeveLength;

    @ManyToOne
    @JoinColumn(name = "cust_code", referencedColumnName = "cust_code")
    private Customer customer;

    @ManyToOne
    @JoinColumn(name = "attire_code", referencedColumnName = "attire_code")
    private Attire attire;

    @ManyToOne
    @JoinColumn(name= "category_code", referencedColumnName = "category_code")
    private Category category;


}
