package com.example.FabriqBackend.service;

import com.example.FabriqBackend.model.Login;
import jakarta.servlet.http.HttpServletResponse;

public interface IUserService {
    Login registerUser(Login user);
    String verify(Login user ,  HttpServletResponse response);
    Login getByUsername(String username);
}
