package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.ChangePasswordDto;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.impl.UserServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
@AutoConfigureMockMvc(addFilters = false) // 🔥 disables security
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private UserServiceImpl userService;

    @Autowired
    private ObjectMapper objectMapper;

    // ✅ 1. Register User
    @Test
    void testRegisterUser() throws Exception {

        Login user = new Login();
        user.setUsername("testuser");
        user.setPassword("1234");

        when(userService.registerUser(any(Login.class)))
                .thenReturn(user);

        mockMvc.perform(post("/v1/user/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    // ✅ 2. Login User
    @Test
    void testLoginUser() throws Exception {

        Login user = new Login();
        user.setUsername("testuser");
        user.setPassword("1234");

        when(userService.verify(any(Login.class), any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenAnswer(i -> ResponseEntity.ok("Login success"));

        mockMvc.perform(post("/v1/user/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user)))
                .andExpect(status().isOk());
    }

    // ✅ 3. Logout
    @Test
    void testLogout() throws Exception {

        when(userService.logout(any(HttpServletResponse.class)))
                .thenReturn("Logged out");

        mockMvc.perform(post("/v1/user/logout"))
                .andExpect(status().isOk())
                .andExpect(content().string("Logged out"));
    }

    // ✅ 4. Refresh Token
    @Test
    void testRefreshToken() throws Exception {

        when(userService.refreshAccessToken(any(HttpServletRequest.class), any(HttpServletResponse.class)))
                .thenAnswer(i -> ResponseEntity.ok("Token refreshed"));

        mockMvc.perform(post("/v1/user/refresh"))
                .andExpect(status().isOk());
    }

    // ✅ 5. Token Status
    @Test
    void testTokenStatus() throws Exception {

        when(userService.checkTokenStatus(any(HttpServletRequest.class)))
                .thenAnswer(i -> ResponseEntity.ok("Valid"));

        mockMvc.perform(get("/v1/user/token-status"))
                .andExpect(status().isOk());
    }

    // ✅ 6. Change Password
    @Test
    void testChangePassword() throws Exception {

        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setCurrentPassword("1234"); // ✅ FIXED
        dto.setNewPassword("5678");

        when(userService.changePassword(any(ChangePasswordDto.class)))
                .thenAnswer(i -> ResponseEntity.ok("Password changed"));

        mockMvc.perform(post("/v1/user/change-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }
}