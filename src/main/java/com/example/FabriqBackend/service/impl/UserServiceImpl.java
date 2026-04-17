package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.dto.ChangePasswordDto;
import com.example.FabriqBackend.exception.ResourceNotFoundException;
import com.example.FabriqBackend.exception.UnauthorizedException;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.RefreshToken;
import com.example.FabriqBackend.model.UserPrincipal;
import com.example.FabriqBackend.service.Interface.IUserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Slf4j
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements IUserService {


    private final JWTService jwtService;
    private final RefreshTokenService refreshTokenService;
    final AuthenticationManager authManager;
    private final UserDao userDao;


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @CachePut(key = "#result.tenantId + ':' + #result.username")
    public Login registerUser(Login user) {
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        String tenantId = TenantContext.getCurrentTenant();
        if (!StringUtils.hasText(tenantId)) {
            log.error("Registration failed: Missing Tenant ID in header");
            throw new IllegalArgumentException("Tenant ID is required. Please provide X-Tenant-ID header.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setTenantId(tenantId);
        userDao.save(user);
        log.info("Successfully registered user: {} under tenant: {}", user.getUsername(), tenantId);
        return user;
    }

    public ResponseEntity<?> verify(Login user, HttpServletRequest request, HttpServletResponse response) {
        log.info("Verifying credentials for username: {}", user.getUsername());
        try {
            Authentication authentication = authManager.authenticate(
                    new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
            );
            if (authentication.isAuthenticated()) {
                log.info("Authentication successful for user: {}", user.getUsername());
                Login authenticatedUser = userDao.findByUsername(user.getUsername());

                String accessToken = jwtService.generateAccessToken(
                    user.getUsername(),
                    authenticatedUser.getTenantId(),
                    authenticatedUser.getId(),
                    authenticatedUser.getRole()
                );

            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(
                user.getUsername(),
                authenticatedUser.getTenantId(),
                request
            );
            String refreshToken = refreshTokenEntity.getToken();

            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge((int) (jwtService.getAccessTokenValidity() / 1000))
                    .build();

            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("None")
                    .maxAge((int) (jwtService.getRefreshTokenValidity() / 1000))
                    .build();

                response.addHeader("Set-Cookie", accessCookie.toString());
                response.addHeader("Set-Cookie", refreshCookie.toString());

            return ResponseEntity.ok().build();
            } else {
                log.warn("Authentication failed for user: {}", user.getUsername());
                throw new UnauthorizedException("Invalid credentials");
            }
        } catch (Exception e) {
            log.error("Authentication error for user: {}", user.getUsername(), e);
            throw new UnauthorizedException("Invalid credentials", e);
        }
    }

    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #username + ':retrieved by username'")
    public Login getByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public String logout(HttpServletResponse response) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            refreshTokenService.revokeAllUserTokens(username);
        }

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();
        
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)
                .path("/")
                .sameSite("Lax")
                .maxAge(0)
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        log.debug("Logout access cookie: {}", accessCookie);
        log.debug("Logout refresh cookie: {}", refreshCookie);
        log.info("Logout successful");
        return "Logout successful";
    }

    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        log.info("Attempting to refresh access token");
        String refreshToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("refreshToken".equals(cookie.getName())) {
                    refreshToken = cookie.getValue();
                    break;
                }
            }
        }

        if (refreshToken == null) {
            String authHeader = request.getHeader("Authorization");
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                refreshToken = authHeader.substring(7);
            }
        }
        if (refreshToken == null) {
            String headerRefresh = request.getHeader("X-Refresh-Token");
            if (headerRefresh != null && !headerRefresh.isBlank()) {
                refreshToken = headerRefresh;
            }
        }

        if (refreshToken == null) {
            return ResponseEntity.status(401).body("Refresh token not found");
        }

        RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken)
                .orElse(null);

        if (validRefreshToken == null) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        Login user = userDao.findByUsername(validRefreshToken.getUsername());
        if (user == null) {
            throw new ResourceNotFoundException("User", "username", validRefreshToken.getUsername());
        }

        String newAccessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getTenantId(),
                user.getId(),
                user.getRole()
        );


        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, request);

        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
            .httpOnly(true)
            .secure(false)
            .path("/")
            .domain("myapp.social")
            .sameSite("Lax")
            .maxAge((int) (jwtService.getAccessTokenValidity() / 1000))
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
            .httpOnly(true)
            .secure(false)
            .path("/")
            .domain("myapp.social")
            .sameSite("Lax")
            .maxAge((int) (jwtService.getRefreshTokenValidity() / 1000))
            .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        log.info("Token refreshed successfully for user: {}", user.getUsername());
        return ResponseEntity.ok("Token refreshed successfully");
    }

    public ResponseEntity<?> checkTokenStatus(HttpServletRequest request) {
        String accessToken = null;
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    accessToken = cookie.getValue();
                    break;
                }
            }
        }

        if (accessToken == null) {
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("status", "NO_TOKEN");
                put("message", "No access token found");
                put("authenticated", false);
            }});
        }

        try {
            String username = jwtService.extractUserName(accessToken);
            String tenantId = jwtService.extractTenantId(accessToken);
            String tokenType = jwtService.extractTokenType(accessToken);

            java.util.Date expiration = jwtService.extractClaim(accessToken, io.jsonwebtoken.Claims::getExpiration);
            java.util.Date issuedAt = jwtService.extractClaim(accessToken, io.jsonwebtoken.Claims::getIssuedAt);

            long currentTime = System.currentTimeMillis();
            long expirationTime = expiration.getTime();
            long timeRemaining = expirationTime - currentTime;
            boolean isExpired = timeRemaining <= 0;

            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("status", isExpired ? "EXPIRED" : "VALID");
                put("message", isExpired ? "Token has expired" : "Token is valid");
                put("authenticated", !isExpired);
                put("username", username);
                put("tenantId", tenantId);
                put("tokenType", tokenType);
                put("issuedAt", issuedAt.toInstant().toString());
                put("expiresAt", expiration.toInstant().toString());
                put("timeRemaining", timeRemaining > 0 ? (timeRemaining / 1000) + " seconds" : "0 seconds (expired)");
                put("needsRefresh", timeRemaining < 60000); // Less than 1 minute remaining
            }});

        } catch (Exception e) {
            return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
                put("status", "INVALID");
                put("message", "Token is invalid or malformed: " + e.getMessage());
                put("authenticated", false);
            }});
        }
    }

    public ResponseEntity<?> changePassword(ChangePasswordDto changePasswordDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal userPrincipal)) {
            return ResponseEntity.status(401).body("Invalid authentication principal");
        }

        String username = userPrincipal.getUsername();
        Login user = userDao.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        if (!encoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("Current password is incorrect");
        }

        if (!StringUtils.hasText(changePasswordDto.getNewPassword())) {
            return ResponseEntity.status(400).body("New password cannot be blank");
        }

        if (changePasswordDto.getNewPassword().length() < 6) {
            return ResponseEntity.status(400).body("New password must be at least 6 characters long");
        }

        if (encoder.matches(changePasswordDto.getNewPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("New password must be different from current password");
        }

        user.setPassword(encoder.encode(changePasswordDto.getNewPassword()));
        userDao.save(user);

        refreshTokenService.revokeAllUserTokens(username);
        log.info("Password changed successfully for user: {}", username);

        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Password changed successfully");
            put("success", true);
        }});
    }

}
