package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.RefreshToken;
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
                    .secure(false)   // ⚠️ Set to true in production with HTTPS
                    .path("/")       // ✅ Available for all endpoints
                    .sameSite("Lax") // ✅ CSRF protection
                    .maxAge((int) (jwtService.getAccessTokenValidity() / 1000)) // 15 minutes
                    .build();

            // Refresh Token Cookie (7 days)
            ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", refreshToken)
                    .httpOnly(true)  // ✅ Prevents XSS attacks
                    .secure(false)   // ⚠️ Set to true in production with HTTPS
                    .path("/")       // ✅ Available for all endpoints
                    .sameSite("Lax") // ✅ CSRF protection
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
        return "Logout successful";
    }

    /**
     * Refresh access token using refresh token
     */
    public ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
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

        if (refreshToken == null) {
            throw new RuntimeException("Refresh token not found");
        }

        // Verify refresh token
        RefreshToken validRefreshToken = refreshTokenService.verifyRefreshToken(refreshToken)
                .orElseThrow(() -> new RuntimeException("Invalid or expired refresh token"));

        // Get user details
        Login user = userDao.findByUsername(validRefreshToken.getUsername());
        if (user == null) {
            throw new RuntimeException("User not found");
        }

        // Log user details before generating new token
        System.out.println("🔄 REFRESH TOKEN - User loaded: " + user.getUsername() +
                          ", TenantId: " + user.getTenantId() +
                          ", UserId: " + user.getId() +
                          ", Role: " + user.getRole());

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getTenantId(),
                user.getId(),
                user.getRole()
        );

        System.out.println("✅ NEW ACCESS TOKEN GENERATED with TenantId: " + user.getTenantId());

        // Rotate refresh token (security best practice)
        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshToken, request);

        // Set new cookies
        ResponseCookie accessCookie = ResponseCookie.from("accessToken", newAccessToken)
                .httpOnly(true)
                .secure(false)  // Set to true in production
                .path("/")
                .sameSite("Lax")
                .maxAge((int) (jwtService.getAccessTokenValidity() / 1000))
                .build();

        ResponseCookie refreshCookie = ResponseCookie.from("refreshToken", newRefreshToken.getToken())
                .httpOnly(true)
                .secure(false)  // Set to true in production
                .path("/")
                .sameSite("Lax")
                .maxAge((int) (jwtService.getRefreshTokenValidity() / 1000))
                .build();

        response.addHeader("Set-Cookie", accessCookie.toString());
        response.addHeader("Set-Cookie", refreshCookie.toString());

        // Return response with refresh information
        return ResponseEntity.ok(new java.util.HashMap<String, Object>() {{
            put("message", "Tokens refreshed successfully");
            put("username", user.getUsername());
            put("tenantId", user.getTenantId());
            put("refreshedAt", java.time.Instant.now().toString());
            put("accessTokenExpiresIn", jwtService.getAccessTokenValidity() / 1000 + " seconds");
            put("refreshTokenExpiresIn", jwtService.getRefreshTokenValidity() / 1000 + " seconds");
        }});
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

}
