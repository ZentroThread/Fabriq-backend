package com.example.FabriqBackend.config;

import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.userDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger logger = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();
        logger.debug("🔐 JwtFilter processing request: {}", requestURI);

        String token = null;

        // Extract JWT from HttpOnly cookie (now using accessToken)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("Access token found in cookie for request: {}", requestURI);
                    break;
                }
            }
        }

        if (token == null) {
            logger.debug("No access token found in cookies for request: {}", requestURI);
        }

        // Authenticate only if token exists and user not already authenticated
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                // Extract username from token
                String username = jwtService.extractUserName(token);
                String tenantIdFromToken = jwtService.extractTenantId(token);
                logger.debug("Extracted from token - Username: {}, TenantId: {}", username, tenantIdFromToken);

                UserDetails userDetails =
                        context.getBean(userDetailsService.class)
                                .loadUserByUsername(username);

                // Log UserPrincipal details
                if (userDetails instanceof com.example.FabriqBackend.model.UserPrincipal userPrincipal) {
                    logger.debug("UserPrincipal loaded - Username: {}, TenantId: {}",
                        userPrincipal.getUsername(), userPrincipal.getTenantId());
                }

                if (jwtService.validateAccessToken(token, userDetails)) {
                    // Authentication
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
                    logger.info("✅ Authentication set successfully for user: {} with tenantId: {} on request: {}",
                        username, tenantIdFromToken, requestURI);
                } else {
                    logger.warn("JWT token validation failed for user: {}", username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.setContentType("application/json");
                    response.getWriter().write("{\"error\": \"Invalid or expired token\", \"message\": \"Please refresh your token\"}");
                    return;
                }

            } catch (ExpiredJwtException e) {
                logger.error("JWT token expired: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Token expired\", \"message\": \"Your session has expired. Please refresh your token\"}");
                return;
            } catch (MalformedJwtException e) {
                logger.error("Malformed JWT token: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Malformed token\", \"message\": \"Invalid token format\"}");
                return;
            } catch (SignatureException e) {
                logger.error("JWT signature validation failed: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Invalid signature\", \"message\": \"Token signature validation failed\"}");
                return;
            } catch (Exception e) {
                logger.error("JWT token validation error: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.setContentType("application/json");
                response.getWriter().write("{\"error\": \"Authentication failed\", \"message\": \"" + e.getMessage() + "\"}");
                return;
            }
        }

        // Continue filter chain - TenantFilter will handle tenant cleanup after request
        filterChain.doFilter(request, response);
    }
}
