package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long  id;

    private String name;
    private String email;
    private String password; // nullable for Google users
    private String role = "CUSTOMER";     // CUSTOMER / OWNER / CASHIER
    private String provider; // GOOGLE or LOCAL

}
