package com.example.FabriqBackend.model.salary;

import com.example.FabriqBackend.enums.HolidayCategoryEnum;
import com.example.FabriqBackend.enums.HolidayPayType;
import com.example.FabriqBackend.model.TenantAwareEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "holidays")
public class Holiday extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String date;
    private String description;

    @Enumerated(EnumType.STRING)
    private HolidayCategoryEnum category;

    @Enumerated(EnumType.STRING)
    private HolidayPayType payType;

}
