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
import java.util.List;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/v1/user/login",
            "/v1/user/register",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            String tenantId = null;

            // PRIORITY 1: Manual override (internal tools / admin use)
            String headerTenantId = request.getHeader("X-Tenant-ID");
            if (headerTenantId != null && !headerTenantId.isBlank()) {
                tenantId = headerTenantId;
                System.out.println("Tenant ID set from X-Tenant-ID header: " + tenantId);
            }

            // PRIORITY 2: From authenticated user
            if (tenantId == null) {
                Authentication authentication =
                        SecurityContextHolder.getContext().getAuthentication();

                if (authentication != null &&
                        authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {

                    tenantId = userPrincipal.getTenantId();
                    System.out.println("Tenant ID set from authenticated user: " + tenantId);
                }
            }

            // Set tenant context
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setCurrentTenant(tenantId);
            }

            filterChain.doFilter(request, response);

        } finally {
            // CRITICAL: prevent tenant leakage across threads
            TenantContext.clear();
        }
    }
}
