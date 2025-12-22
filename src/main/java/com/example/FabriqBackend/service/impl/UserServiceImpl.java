package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.dto.TokenResponse;
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
        System.out.println("Registering user for tenant: " + tenantId);
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("Tenant ID is required. Please provide X-Tenant-ID header.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setTenantId(tenantId);
        userDao.save(user);
        return user;
    }

    public TokenResponse verify(Login user, HttpServletRequest request, HttpServletResponse response) {

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
            
            System.out.println("🍪 Access token cookie created (15 min expiry)");
            System.out.println("🍪 Refresh token cookie created (7 days expiry)");
            
            return new TokenResponse(
                accessToken,
                refreshToken,
                jwtService.getAccessTokenValidity(),
                jwtService.getRefreshTokenValidity()
            );
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
            System.out.println("🔒 Revoked all refresh tokens for user: " + username);
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
        System.out.println("🍪 Cookies cleared - User logged out");
        return "Logout successful";
    }

    /**
     * Refresh access token using refresh token
     */
    public TokenResponse refreshAccessToken(HttpServletRequest request, HttpServletResponse response) {
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

        // Generate new access token
        String newAccessToken = jwtService.generateAccessToken(
                user.getUsername(),
                user.getTenantId(),
                user.getId(),
                user.getRole()
        );

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

        System.out.println("🔄 Tokens refreshed for user: " + user.getUsername());

        return new TokenResponse(
                newAccessToken,
                newRefreshToken.getToken(),
                jwtService.getAccessTokenValidity(),
                jwtService.getRefreshTokenValidity()
        );
    }

}
