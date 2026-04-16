package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;


@Slf4j
@Component
public class JWTService {

    @Value("${jwt.secret.key:}")
    private String secretKey;

    private static final long ACCESS_TOKEN_VALIDITY = 60 * 60 * 1000;
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000;

    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            // generate a 256-bit (32 byte) key
            byte[] key = new byte[32];
            new SecureRandom().nextBytes(key);
            secretKey = Base64.getEncoder().encodeToString(key);
        }
    }

    public String generateAccessToken(String username, String tenantId, Integer userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId);
        claims.put("userId", userId);
        claims.put("role", role);
        claims.put("type", "access");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_TOKEN_VALIDITY))
                .and()
                .signWith(getKey())
                .compact();
    }


    public String generateRefreshToken(String username) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("type", "refresh");

        return Jwts.builder()
                .claims()
                .add(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + REFRESH_TOKEN_VALIDITY))
                .and()
                .signWith(getKey())
                .compact();
    }

    @Deprecated
    public String generateToken(String username, String tenantId, Integer userId, String role) {
        return generateAccessToken(username, tenantId, userId, role);
    }

    public long getAccessTokenValidity() {
        return ACCESS_TOKEN_VALIDITY;
    }

    public long getRefreshTokenValidity() {
        return REFRESH_TOKEN_VALIDITY;
    }

    private SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractTenantId(String token) {
        return extractClaim(token, claims -> claims.get("tenantId", String.class));
    }

    public String extractTokenType(String token) {
        return extractClaim(token, claims -> claims.get("type", String.class));
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        return (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    public boolean validateAccessToken(String token, UserDetails userDetails) {
        try {
            final String userName = extractUserName(token);
            final String tokenType = extractTokenType(token);
            final boolean isExpired = isTokenExpired(token);
            if (isExpired) {
                return false;
            }

            if (!"access".equals(tokenType)) {
                return false;
            }

            if (!userName.equals(userDetails.getUsername())) {
                return false;
            }

            return true;
        } catch (Exception e) {
            log.error("Failed to validate access token: {}", e.getMessage(), e);
            return false;
        }
    }

    public boolean validateRefreshToken(String token) {
        try {
            final String tokenType = extractTokenType(token);


            return !isTokenExpired(token) && "refresh".equals(tokenType);

        } catch (Exception e) {
            log.error("Failed to validate refresh token: {}", e.getMessage(), e);
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public String generateToken(User user) {
        return generateAccessToken(user.getEmail(), "PUBLIC", user.getId().intValue(), user.getRole());
    }
}