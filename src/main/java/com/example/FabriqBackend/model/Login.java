package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@Data
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "login")
public class Login extends TenantAwareEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String username;
    private String password;
    private String role;

    // tenantId is inherited from TenantAwareEntity
}
