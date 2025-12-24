package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    
    Optional<RefreshToken> findByToken(String token);
    
    Optional<RefreshToken> findByUsernameAndRevokedFalse(String username);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.username = ?1")
    void deleteByUsername(String username);
    
    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshToken rt WHERE rt.expiryDate < ?1")
    int deleteByExpiryDateBefore(Instant now);
    
    @Transactional
    @Modifying
    @Query("UPDATE RefreshToken rt SET rt.revoked = true WHERE rt.username = ?1")
    void revokeAllByUsername(String username);
}
