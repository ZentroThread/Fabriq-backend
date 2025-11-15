package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "attire")
public class Attire extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
//if use .AUTO it expects Long type
    private Integer id;
    private Integer attireCode;
    private String attireName;
    private Double attirePrice;
    private String attireType;

    @ManyToOne
    @JoinColumn(name = "category_id" , referencedColumnName = "category_id")
    private Category category;
}
