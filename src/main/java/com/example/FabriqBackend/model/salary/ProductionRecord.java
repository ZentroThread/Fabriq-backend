package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.model.Employee;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "production_record")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class ProductionRecord extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate date;
    private String productionName;
    private Integer quantity;
    private Double ratePerProduct;

    @ManyToOne
    @JoinColumn(name="emp_id", nullable=false)
    private Employee employee;

}
