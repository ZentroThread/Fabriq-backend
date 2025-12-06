package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.enums.DeductionTypeEnum;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "deduction_types")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class DeductionType extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long deductionId;

    private String name;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private DeductionTypeEnum type;

}
