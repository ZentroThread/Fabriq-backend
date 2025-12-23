package com.example.FabriqBackend.config.Tenant;

import com.example.FabriqBackend.model.UserPrincipal;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class TenantFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(TenantFilter.class);

    private static final List<String> PUBLIC_ENDPOINTS = List.of(
            "/v1/user/login",
            //"/v1/user/register",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-resources",
            "/webjars"
    );

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        boolean shouldSkip = PUBLIC_ENDPOINTS.stream().anyMatch(path::startsWith);
        if (shouldSkip) {
            logger.debug("Skipping TenantFilter for public endpoint: {}", path);
        }
        return shouldSkip;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.debug("TenantFilter processing request: {}", requestURI);

        try {
            String tenantId = null;

            // Get tenant from authenticated user
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            logger.debug("Authentication object: {}", authentication != null ? authentication.getClass().getSimpleName() : "null");
            logger.debug("Is authenticated: {}", authentication != null && authentication.isAuthenticated());
            logger.debug("Principal type: {}", authentication != null ? authentication.getPrincipal().getClass().getSimpleName() : "null");

            if (authentication != null &&
                    authentication.getPrincipal() instanceof UserPrincipal userPrincipal) {
                tenantId = userPrincipal.getTenantId();
                logger.info("✅ Tenant ID extracted from UserPrincipal: {} for user: {} on request: {}",
                    tenantId, userPrincipal.getUsername(), requestURI);
            } else {
                logger.warn("❌ No UserPrincipal found in authentication for request: {}", requestURI);
            }

            // Set tenant context
            if (tenantId != null && !tenantId.isBlank()) {
                TenantContext.setCurrentTenant(tenantId);
                logger.debug("Tenant context set to: {}", tenantId);
            } else {
                logger.error("🚨 Tenant ID is NULL or BLANK for request: {} - Data will be empty!", requestURI);
            }

            filterChain.doFilter(request, response);

        } finally {
            // CRITICAL: prevent tenant leakage across threads
            String clearedTenant = TenantContext.getCurrentTenant();
            TenantContext.clear();
            logger.debug("Tenant context cleared (was: {})", clearedTenant);
        }
    }
}