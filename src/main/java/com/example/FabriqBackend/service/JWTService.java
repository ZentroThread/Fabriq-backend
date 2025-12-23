package com.example.FabriqBackend.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
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


@Component
public class JWTService {

    @Value("${jwt.secret.key:}")
    private String secretKey;

    // Token expiration times
    private static final long ACCESS_TOKEN_VALIDITY = 1 * 60 * 1000; // 15 minutes
    private static final long REFRESH_TOKEN_VALIDITY = 7 * 24 * 60 * 60 * 1000; // 7 days

    // Initialize secretKey after properties are set. If none provided, generate a 256-bit key and warn.
    @PostConstruct
    public void init() {
        if (secretKey == null || secretKey.isBlank()) {
            // generate a 256-bit (32 byte) key
            byte[] key = new byte[32];
            new SecureRandom().nextBytes(key);
            secretKey = Base64.getEncoder().encodeToString(key);
        }
    }

    /**
     * Generate access token with configured expiration
     */
    public String generateAccessToken(String username, String tenantId, Integer userId, String role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("tenantId", tenantId);
        claims.put("userId", userId); // Store as Integer directly
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

    /**
     * Generate refresh token with 7 days expiration
     */
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

    /**
     * @deprecated Use generateAccessToken instead
     */
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

    public Integer extractUserId(String token) {
        // Extract as Integer to match the type stored in the token
        return extractClaim(token, claims -> claims.get("userId", Integer.class));
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
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

    /**
     * Validate access token - checks both expiration and token type
     */
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
            return false;
        }
    }

    /**
     * Validate refresh token - checks expiration and token type
     */
    public boolean validateRefreshToken(String token) {
        try {
            final String tokenType = extractTokenType(token);
            return !isTokenExpired(token) && "refresh".equals(tokenType);
        } catch (Exception e) {
            return false;
        }
    }

    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
}