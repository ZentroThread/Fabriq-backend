package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.service.IUserService;
import com.example.FabriqBackend.service.JWTService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseCookie;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements IUserService {


    private final JWTService jwtService;
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

    public String verify(Login user, HttpServletResponse response) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            // Get the authenticated user's details
            Login authenticatedUser = userDao.findByUsername(user.getUsername());
            String token =  jwtService.generateToken(
                user.getUsername(), 
                authenticatedUser.getTenantId(),
                authenticatedUser.getId(),
                authenticatedUser.getRole()
            );
            
            // Create HttpOnly cookie with JWT token
            // 🔒 SECURITY BEST PRACTICES:
            // - httpOnly(true): Prevents JavaScript access (XSS protection)
            // - secure(false for dev, true for prod): Only sends over HTTPS in production
            // - sameSite("Lax"): Balanced CSRF protection (allows navigation, blocks cross-site POST)
            // - path("/"): Cookie accessible across entire application
            // - maxAge: Token expiration (24 hours)
            ResponseCookie cookie = ResponseCookie.from("token", token)
                    .httpOnly(true)  // ✅ Prevents XSS attacks
                    .secure(false)   // ⚠️ Set to true in production with HTTPS
                    .path("/")       // ✅ Available for all endpoints
                    .sameSite("Lax") // ✅ CSRF protection (use "None" if cross-site + add secure(true))
                    .maxAge(24 * 60 * 60) // 24 hours
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            System.out.println("🍪 Cookie created: " + cookie.getName() + " (HttpOnly: " + cookie.isHttpOnly() + ")");
            return "Login successful";
        } else {
            return "Invalid credentials";
        }
    }

    // Read by tenant + username
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #username + ':retrieved by username'")
    public Login getByUsername(String username) {
        return userDao.findByUsername(username);
    }

    public String logout(HttpServletResponse response) {
        // Clear the JWT cookie by setting maxAge to 0
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false)  // Set to true in production
                .path("/")
                .sameSite("Lax")
                .maxAge(0)  // Immediately expire the cookie
                .build();

        response.addHeader("Set-Cookie", cookie.toString());
        System.out.println("🍪 Cookie cleared - User logged out");
        return "Logout successful";
    }

}
