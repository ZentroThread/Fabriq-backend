package com.example.FabriqBackend.config;

import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.userDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JWTService jwtService;
    private final userDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = null;

        // Try Authorization header first
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
        }

        //  Fallback to cookies
        if (token == null) {
            Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if ("accessToken".equals(cookie.getName())) {
                        token = cookie.getValue();
                        break;
                    }
                }
            }
        }

        //   Authenticate
        if (token != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {

                System.out.println("🔐 Token: " + token);

                String username = jwtService.extractUserName(token);
                System.out.println("👤 Username from JWT: " + username);

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.validateAccessToken(token, userDetails)) {

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

                    //System.out.println(" Authentication SUCCESS");

                } else {
                   // System.out.println(" Token validation failed");
                }

            } catch (Exception e) {
                //System.out.println(" JWT ERROR: " + e.getMessage());
                e.printStackTrace(); //  IMPORTANT
            }
        }

        filterChain.doFilter(request, response);
    }
}