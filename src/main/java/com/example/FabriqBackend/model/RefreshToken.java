package com.example.FabriqBackend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

/**
 * Entity for storing refresh tokens in database
 * Refresh tokens have longer lifespan and can be revoked
 */
@Entity
@Table(name = "refresh_tokens")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RefreshToken {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 500)
    private String token;
    
    @Column(nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String tenantId;
    
    @Column(nullable = false)
    private Instant expiryDate;
    
    @Column(nullable = false)
    private Instant createdAt;
    
    @Column(nullable = false)
    private boolean revoked = false;
    
    // IP address and user agent for security tracking
    private String ipAddress;
    private String userAgent;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
    }
}
