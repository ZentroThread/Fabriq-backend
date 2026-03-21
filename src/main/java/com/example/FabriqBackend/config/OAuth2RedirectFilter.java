package com.example.FabriqBackend.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class OAuth2RedirectFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String redirect = request.getParameter("redirect");

        if (redirect != null) {
            request.getSession().setAttribute("REDIRECT_URL", redirect);
            System.out.println("Saved redirect in session: " + redirect);
        }

        filterChain.doFilter(request, response);
    }
}
