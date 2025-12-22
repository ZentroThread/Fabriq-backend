package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.RefreshTokenRepository;
import com.example.FabriqBackend.model.RefreshToken;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

/**
 * Service for managing refresh tokens
 * Handles creation, validation, rotation, and cleanup of refresh tokens
 */
@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTService jwtService;
    
    /**
     * Create a new refresh token for a user
     * Revokes all existing tokens for the user (single device session)
     */
    @Transactional
    public RefreshToken createRefreshToken(String username, String tenantId, HttpServletRequest request) {
        // Revoke all existing refresh tokens for this user (single session)
        // For multi-device support, remove this line
        refreshTokenRepository.revokeAllByUsername(username);
        
        String token = jwtService.generateRefreshToken(username);
        
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(token);
        refreshToken.setUsername(username);
        refreshToken.setTenantId(tenantId);
        refreshToken.setExpiryDate(Instant.now().plusMillis(jwtService.getRefreshTokenValidity()));
        refreshToken.setRevoked(false);
        
        // Track security information
        refreshToken.setIpAddress(getClientIP(request));
        refreshToken.setUserAgent(request.getHeader("User-Agent"));
        
        return refreshTokenRepository.save(refreshToken);
    }
    
    /**
     * Verify refresh token is valid, not expired, and not revoked
     */
    public Optional<RefreshToken> verifyRefreshToken(String token) {
        return refreshTokenRepository.findByToken(token)
                .filter(rt -> !rt.isRevoked())
                .filter(rt -> rt.getExpiryDate().isAfter(Instant.now()))
                .filter(rt -> jwtService.validateRefreshToken(token));
    }
    
    /**
     * Rotate refresh token - creates new token and revokes old one
     * This is a security best practice
     */
    @Transactional
    public RefreshToken rotateRefreshToken(String oldToken, HttpServletRequest request) {
        Optional<RefreshToken> existingToken = verifyRefreshToken(oldToken);
        
        if (existingToken.isEmpty()) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }
        
        RefreshToken oldRefreshToken = existingToken.get();
        
        // Revoke old token
        oldRefreshToken.setRevoked(true);
        refreshTokenRepository.save(oldRefreshToken);
        
        // Create new refresh token
        return createRefreshToken(
            oldRefreshToken.getUsername(), 
            oldRefreshToken.getTenantId(),
            request
        );
    }
    
    /**
     * Revoke all refresh tokens for a user (logout)
     */
    @Transactional
    public void revokeAllUserTokens(String username) {
        refreshTokenRepository.revokeAllByUsername(username);
    }
    
    /**
     * Revoke specific token
     */
    @Transactional
    public void revokeToken(String token) {
        refreshTokenRepository.findByToken(token).ifPresent(rt -> {
            rt.setRevoked(true);
            refreshTokenRepository.save(rt);
        });
    }
    
    /**
     * Scheduled task to clean up expired tokens
     * Runs every day at 3 AM
     */
    @Scheduled(cron = "0 0 3 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        int deleted = refreshTokenRepository.deleteByExpiryDateBefore(Instant.now());
        if (deleted > 0) {
            System.out.println("🧹 Cleaned up " + deleted + " expired refresh tokens");
        }
    }
    
    /**
     * Extract client IP address from request
     */
    private String getClientIP(HttpServletRequest request) {
        String xfHeader = request.getHeader("X-Forwarded-For");
        if (xfHeader == null) {
            return request.getRemoteAddr();
        }
        return xfHeader.split(",")[0];
    }
}
