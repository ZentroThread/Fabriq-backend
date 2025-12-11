package com.example.FabriqBackend.config.Tenant;

import com.example.FabriqBackend.model.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Component
public class TenantFilter extends OncePerRequestFilter {

    // Public endpoints that don't require tenant context
    private static final List<String> PUBLIC_ENDPOINTS = Arrays.asList(
        "/api/v1/user/login",
        "/v1/user/register",
        "/v3/api-docs",
        "/swagger-ui",
        "/swagger-resources",
        "/webjars"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_ENDPOINTS.contains(path);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String requestPath = request.getRequestURI();
        System.out.println("🌐 TenantFilter: Processing request: " + request.getMethod() + " " + requestPath);
        
        try {
            String tenantId = null;

            // PRIORITY 1: Manual tenant ID from header (allows manual override)
            String headerTenantId = request.getHeader("X-Tenant-ID");
            if (headerTenantId != null && !headerTenantId.isEmpty()) {
                tenantId = headerTenantId;
                System.out.println("📋 TenantFilter: Using MANUAL tenantId from X-Tenant-ID header: " + tenantId);
            }

            // PRIORITY 2: If no manual override, check if tenant is already set by JwtFilter
            if (tenantId == null) {
                tenantId = TenantContext.getCurrentTenant();
                if (tenantId != null) {
                    System.out.println("🔑 TenantFilter: Using tenantId from JwtFilter: " + tenantId);
                }
            }

            // PRIORITY 3: Try to get tenant from authenticated user
            if (tenantId == null) {
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                    UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                    tenantId = userPrincipal.getTenantId();
                    System.out.println("👤 TenantFilter: Got tenantId from UserPrincipal: " + tenantId);
                }
            }

            // Set tenant context if we found a tenant ID
            if (tenantId != null && !tenantId.isEmpty()) {
                TenantContext.setCurrentTenant(tenantId);
                System.out.println("✅ TenantFilter: Final tenantId set in context: " + tenantId);
            } else {
                System.out.println("❌ TenantFilter: No tenantId found from any source");
            }

            filterChain.doFilter(request, response);
        } finally {
            // Always clear the context after request completes to prevent thread leaks
            System.out.println("🧹 TenantFilter: Clearing tenant context");
            TenantContext.clear();
        }
    }
}
