package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.enums.AllowanceTypeEnum;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "allowance_type")
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
public class AllowanceType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long allowanceId;

    private String name;
    private Double amount;

    @Enumerated(EnumType.STRING)
    private AllowanceTypeEnum type;
}
