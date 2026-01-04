package com.example.FabriqBackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@Data
@EqualsAndHashCode(callSuper = false, onlyExplicitlyIncluded = true)
@Entity
@Table(name = "attire")
public class Attire extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    @EqualsAndHashCode.Include
    private Integer id;

    @Column(name = "attire_code", unique = true)
    private String attireCode;
    private String attireName;
    private String attireDescription;
    private Double attirePrice;
    private String attireStatus;
    private Integer attireStock;
    @Column(name = "image_url")
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "category_id", referencedColumnName = "category_id")
    private Category category;

    @OneToMany(mappedBy = "attire", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<AttireRent> rentals; // ← ADD THIS
}
