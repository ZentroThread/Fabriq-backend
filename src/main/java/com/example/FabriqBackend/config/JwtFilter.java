package com.example.FabriqBackend.config;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.userDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(0)
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;
        String username = null;

        // Extract JWT from HttpOnly cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Authenticate only if token exists and user not already authenticated
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                username = jwtService.extractUserName(token);

                UserDetails userDetails =
                        context.getBean(userDetailsService.class)
                                .loadUserByUsername(username);

                if (jwtService.validateToken(token, userDetails)) {
                    // Extract and set tenant ID from JWT token
                    String tenantId = jwtService.extractTenantId(token);
                    if (tenantId != null) {
                        TenantContext.setCurrentTenant(tenantId);
                    }

                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails,
                                    null,
                                    userDetails.getAuthorities()
                            );

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    SecurityContextHolder.getContext().setAuthentication(authToken);
                }

            } catch (Exception e) {
                // Invalid token → user remains unauthenticated
                System.err.println("❌ [JWT FILTER] Token validation failed: " + e.getMessage());
                TenantContext.clear();
            }
        } else if (token == null) {
            // No token → ensure tenant context is clear
            TenantContext.clear();
        }

        try {
            filterChain.doFilter(request, response);
        } finally {
            // 🔥 CRITICAL: Clear tenant context ONLY after response is committed
            // This prevents race conditions with @Cacheable key evaluation
            String currentTenant = TenantContext.getCurrentTenant();
            if (currentTenant != null) {
                System.out.println("🧹 [JWT FILTER] Clearing tenant context: " + currentTenant);
            }
            TenantContext.clear();
        }
    }
}
