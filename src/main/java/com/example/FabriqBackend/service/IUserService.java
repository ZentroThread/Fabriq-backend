package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dto.ChangePasswordDto;
import com.example.FabriqBackend.model.Login;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

public interface IUserService {
    Login registerUser(Login user);
    ResponseEntity<?> verify(Login user, HttpServletRequest request, HttpServletResponse response);
    Login getByUsername(String username);
    String logout(HttpServletResponse response);
    ResponseEntity<?> refreshAccessToken(HttpServletRequest request, HttpServletResponse response);
    ResponseEntity<?> checkTokenStatus(HttpServletRequest request);
    ResponseEntity<?> changePassword(ChangePasswordDto changePasswordDto);
}
