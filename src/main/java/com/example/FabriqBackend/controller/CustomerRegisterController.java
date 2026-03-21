package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dao.CustDao;
import com.example.FabriqBackend.model.User;
import com.example.FabriqBackend.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/v1/customer/auth")
@RequiredArgsConstructor
public class CustomerRegisterController {

    private final CustDao userRepository;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
    private final JWTService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody User user) {

        user.setPassword(encoder.encode(user.getPassword()));
        user.setProvider("LOCAL");

        userRepository.save(user);

        return "User registered";
    }
    @PostMapping("/login")
    public String login(@RequestBody User loginRequest) {

        User user = userRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return jwtService.generateToken(user);
    }

}
