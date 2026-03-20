package com.example.FabriqBackend.config;

import com.example.FabriqBackend.dao.CustDao;
import com.example.FabriqBackend.model.User;
import com.example.FabriqBackend.service.JWTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor

public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

    private final CustDao custDao;
    private final JWTService jwtService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        String email = oAuth2User.getAttribute("email");
        String name = oAuth2User.getAttribute("name");


        User user = custDao.findByEmail(email)
                .orElseGet(() -> {
                    User newUser = new User();
                    newUser.setEmail(email);
                    newUser.setName(name);
                    newUser.setRole("CUSTOMER");
                    newUser.setProvider("GOOGLE");
                    return custDao.save(newUser);
                });

        //  Restrict only CUSTOMER
        if (!"CUSTOMER".equals(user.getRole())) {
            throw new RuntimeException("Only customers can login with Google");
        }

        //  Generate JWT
        String token = jwtService.generateToken(user);

        String state = request.getParameter("state");
        //String redirectUrl = null;

        String redirectUrl = (String) request.getSession().getAttribute("REDIRECT_URL");

        List<String> allowedUrls = List.of(
                "http://localhost:5174",
                "http://localhost:5173",
                "http://localhost:3000",
                "https://client-frontend-azure.vercel.app/"
        );

        System.out.println("REDIRECT FROM SESSION: " + redirectUrl);

        if (redirectUrl == null || allowedUrls.stream().noneMatch(redirectUrl::startsWith)) {
            redirectUrl = "https://client-frontend-azure.vercel.app/";
        }

        response.sendRedirect(redirectUrl + "/oauth-success?token=" + token);
    }
}