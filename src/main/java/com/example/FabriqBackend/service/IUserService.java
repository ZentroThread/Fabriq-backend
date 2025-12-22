package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.TokenResponse;
import com.example.FabriqBackend.model.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public interface IUserService {
    Login registerUser(Login user);
    TokenResponse verify(Login user, HttpServletRequest request, HttpServletResponse response);
    Login getByUsername(String username);
}
