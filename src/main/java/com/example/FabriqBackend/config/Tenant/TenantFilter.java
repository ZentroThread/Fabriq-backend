package com.example.FabriqBackend.config.Tenant;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TenantFilter extends OncePerRequestFilter {

    // ❌ Only skip Swagger & login
    private static final List<String> SKIP_ENDPOINTS = List.of(
            "/v1/user/login",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars",
            "/v1/rag//customer/chat",
            "/v1/rag/feedback",
            "/v1/feedback/approved",
            "/v1/customer/auth/login",
            "/v1/customer/auth/register"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        return SKIP_ENDPOINTS.stream().anyMatch(path::startsWith);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        try {
            String tenantId = request.getHeader("X-Tenant-ID");

            // Look for tenantId in query parameters (e.g. for WebSocket connections)
            if (tenantId == null || tenantId.isBlank()) {
                tenantId = request.getParameter("tenantId");
            }

            // 🌍 If header missing → resolve from public URL
            if (tenantId == null || tenantId.isBlank()) {
                tenantId = resolveTenantFromRequest(request);
            }

            System.out.println("🧭 TenantFilter HIT");
            System.out.println("🧭 URI = " + request.getRequestURI());
            System.out.println("🧭 Resolved Tenant = " + tenantId);

            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setCurrentTenant(tenantId);
            }

            filterChain.doFilter(request, response);

        } finally {
            TenantContext.clear();
        }
    }

    /**
     * Resolve tenant from public URL
     * Example: /v1/public/T001/attire
     */
    private String resolveTenantFromRequest(HttpServletRequest request) {
        String[] parts = request.getRequestURI().split("/");

        // ["", "v1", "public", "T001", "attire"]
        if (parts.length >= 4 && "public".equals(parts[2])) {
            return parts[3];
        }
        return null;
    }
}
