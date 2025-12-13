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

@Component
public class TenantFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
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
