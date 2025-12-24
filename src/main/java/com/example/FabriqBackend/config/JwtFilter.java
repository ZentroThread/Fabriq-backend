package com.example.FabriqBackend.config;

import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.userDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtFilter extends OncePerRequestFilter {


    @Autowired
    private JWTService jwtService;

    @Autowired
    private ApplicationContext context;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String requestURI = request.getRequestURI();

        String token = null;

        // Extract JWT from HttpOnly cookie (now using accessToken)
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("accessToken".equals(cookie.getName())) {
                    token = cookie.getValue();
                    break;
                }
            }
        }

        // Authenticate only if token exists and user not already authenticated
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
            String username = jwtService.extractUserName(token);
            //String tenantIdFromToken = jwtService.extractTenantId(token);

            UserDetails userDetails =
                context.getBean(userDetailsService.class)
                    .loadUserByUsername(username);

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
            }
            } catch (Exception e) {
            // Token is invalid or expired. Do not interrupt the filter chain —
            // allow requests such as the refresh endpoint to proceed so the
            // client can obtain a new access token using a valid refresh token.
            }
        }

        // Continue filter chain - TenantFilter will handle tenant cleanup after request
        filterChain.doFilter(request, response);
    }
}
