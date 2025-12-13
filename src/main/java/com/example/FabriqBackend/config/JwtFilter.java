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
    ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = null;
        String username = null;

        // Extract JWT token from HttpOnly cookie
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("token".equals(cookie.getName())) {
                    token = cookie.getValue();
                    System.out.println("🍪 JwtFilter: Found JWT token in cookie");
                    break;
                }
            }
        }

        // Extract username and tenant from token if found
        if (token != null && !token.isEmpty()) {
            try {
                username = jwtService.extractUserName(token);

                // Extract and set tenant ID from JWT token early
                String tenantId = jwtService.extractTenantId(token);
                if (tenantId != null && !tenantId.isEmpty()) {
                    TenantContext.setCurrentTenant(tenantId);
                    System.out.println("🔑 JwtFilter: Set tenantId from JWT token: " + tenantId);
                } else {
                    System.out.println("⚠️ JwtFilter: No tenantId found in JWT token");
                }
            } catch (Exception e) {
                System.out.println("❌ JwtFilter: Invalid token in cookie - " + e.getMessage());
            }
        } else {
            System.out.println("⚠️ JwtFilter: No JWT token found in cookies");
        }

        // Authenticate user if token is valid
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = context.getBean(userDetailsService.class).loadUserByUsername(username);

            if (jwtService.validateToken(token, userDetails)) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource()
                        .buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
                System.out.println("✅ JwtFilter: User authenticated successfully: " + username);
            }
        }
        filterChain.doFilter(request, response);
    }
}
