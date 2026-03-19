package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.dto.ChangePasswordDto;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.RefreshToken;
import com.example.FabriqBackend.model.UserPrincipal;
import com.example.FabriqBackend.service.IUserService;
import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.RefreshTokenService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
            throw new IllegalArgumentException("Tenant ID is required. Please provide X-Tenant-ID header.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setTenantId(tenantId);
        userDao.save(user);
        return user;
    }

    public ResponseEntity<?> verify(Login user, HttpServletRequest request, HttpServletResponse response) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            // Get the authenticated user's details
            Login authenticatedUser = userDao.findByUsername(user.getUsername());

            // Generate access token (15 minutes)
            String accessToken = jwtService.generateAccessToken(
                user.getUsername(),
                authenticatedUser.getTenantId(),
                authenticatedUser.getId(),
                authenticatedUser.getRole()
            );

            // Generate and store refresh token (7 days)
            RefreshToken refreshTokenEntity = refreshTokenService.createRefreshToken(
                user.getUsername(),
                authenticatedUser.getTenantId(),
                request
            );
            String refreshToken = refreshTokenEntity.getToken();

            // Create HttpOnly cookies for both tokens
            // Access Token Cookie (15 minutes)
            ResponseCookie accessCookie = ResponseCookie.from("accessToken", accessToken)
                    .httpOnly(true)  // ✅ Prevents XSS attacks
                    .secure(true)   // ⚠️ Set to true in production with HTTPS
                    .path("/")       // ✅ Available for all endpoints
                    //.domain("myapp.social") // ✅ CRITICAL: Set cookie domain for cross-subdomain access
                    .sameSite("None") // ✅ CSRF protection
                    .maxAge((int) (jwtService.getAccessTokenValidity() / 1000)) // 15 minutes
                    .build();

            // Refresh Token Cookie (7 days)
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)  // ✅ Prevents XSS attacks
                    .secure(true)   // ⚠️ Set to true in production with HTTPS
                    .path("/")       // ✅ Available for all endpoints
                    //.domain("myapp.social") // ✅ CRITICAL: Set cookie domain for cross-subdomain access
                    .sameSite("None") // ✅ CSRF protection
                    .maxAge((int) (jwtService.getRefreshTokenValidity() / 1000)) // 7 days
                    .build();

            response.addHeader("Set-Cookie", accessCookie.toString());
            response.addHeader("Set-Cookie", refreshCookie.toString());


            return ResponseEntity.ok().build();
        } else {
            throw new RuntimeException("Invalid credentials");
        }
    }

    // Read by tenant + username
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #username + ':retrieved by username'")
    public Login getByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public String logout(HttpServletResponse response) {
        // Get current user and revoke all their refresh tokens
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            refreshTokenService.revokeAllUserTokens(username);
        }
        
        // Clear both access and refresh token cookies
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", "")
                .httpOnly(true)
                .secure(false)  // Set to true in production
                .path("/")
                .sameSite("Lax")
                .maxAge(0)  // Immediately expire
                .build();
        
        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(false)  // Set to true in production
                .path("/")
                .sameSite("Lax")
                .maxAge(0)  // Immediately expire
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());
        System.out.println(accessCookie);
        System.out.println(refreshCookie);
        return "Logout successful";
    }

    /**
     * Refresh access token using refresh token
     */
    public ResponseEntity<String> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
        // Extract refresh token from cookie
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

        // Fallback: allow refresh token via Authorization Bearer header or X-Refresh-Token header
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

        // Verify refresh token
        RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken)
                .orElse(null);

        if (validRefreshToken == null) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        // Get user details
        Login user = userDao.findByUsername(validRefreshToken.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getTenantId(),
                user.getId(),
                user.getRole()
        );


        // Rotate refresh token (security best practice)
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, request);

        // Set new cookies (note: for cross-site XHR you may need SameSite=None and Secure=true in production)
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
            .httpOnly(true)
            .secure(false)  // Set to true in production with HTTPS
            .path("/")
            .domain("myapp.social") // ✅ CRITICAL: Set cookie domain
            .sameSite("Lax")
            .maxAge((int) (jwtService.getAccessTokenValidity() / 1000))
            .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
            .httpOnly(true)
            .secure(false)  // Set to true in production with HTTPS
            .path("/")
            .domain("myapp.social") // ✅ CRITICAL: Set cookie domain
            .sameSite("Lax")
            .maxAge((int) (jwtService.getRefreshTokenValidity() / 1000))
            .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // Return a JSON payload matching frontend TokenResponse schema so the client can update expiry
//        java.util.Map<String, Object> body = new java.util.HashMap<>();
//        body.put("accessToken", newAccessToken);
//        body.put("refreshToken", newRefreshToken.getToken());
//        body.put("tokenType", "Bearer");
//        body.put("accessTokenExpiresIn", jwtService.getAccessTokenValidity());
//        body.put("refreshTokenExpiresIn", jwtService.getRefreshTokenValidity());

        return ResponseEntity.ok("Token refreshed successfully");
    }

    /**
     * Check the status of the current access token
     */
    public ResponseEntity<?> checkTokenStatus(HttpServletRequest request) {
        // Extract access token from cookie
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
            // Extract token information
            String username = jwtService.extractUserName(accessToken);
            String tenantId = jwtService.extractTenantId(accessToken);
            String tokenType = jwtService.extractTokenType(accessToken);

            // Get expiration time
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

    /**
     * Change user password
     */
    public ResponseEntity<?> changePassword(ChangePasswordDto changePasswordDto) {
        // Get current authenticated user
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).body("User not authenticated");
        }

        if (!(authentication.getPrincipal() instanceof UserPrincipal)) {
            return ResponseEntity.status(401).body("Invalid authentication principal");
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String username = userPrincipal.getUsername();

        // Get user from database
        Login user = userDao.findByUsername(username);
        if (user == null) {
            return ResponseEntity.status(404).body("User not found");
        }

        // Verify current password
        if (!encoder.matches(changePasswordDto.getCurrentPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("Current password is incorrect");
        }

        // Validate new password
        if (!StringUtils.hasText(changePasswordDto.getNewPassword())) {
            return ResponseEntity.status(400).body("New password cannot be blank");
        }

        if (changePasswordDto.getNewPassword().length() < 6) {
            return ResponseEntity.status(400).body("New password must be at least 6 characters long");
        }

        // Ensure new password is different from current password
        if (encoder.matches(changePasswordDto.getNewPassword(), user.getPassword())) {
            return ResponseEntity.status(400).body("New password must be different from current password");
        }

        // Update password
        user.setPassword(encoder.encode(changePasswordDto.getNewPassword()));
        userDao.save(user);

        // Revoke all refresh tokens for security
        refreshTokenService.revokeAllUserTokens(username);

        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Password changed successfully");
            put("success", true);
        }});
    }

}
