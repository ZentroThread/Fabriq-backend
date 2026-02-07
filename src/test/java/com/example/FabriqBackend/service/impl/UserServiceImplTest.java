package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.RefreshToken;
import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.RefreshTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock private JWTService jwtService;
    @Mock private RefreshTokenService refreshTokenService;
    @Mock private AuthenticationManager authManager;
    @Mock private UserDao userDao;
    @Mock private HttpServletRequest request;
    @Mock private HttpServletResponse response;
    @Mock private Authentication authentication;

    @InjectMocks
    private UserServiceImpl userService;

    @BeforeEach
    void setupTenant() {
        TenantContext.setCurrentTenant("tenant1");
    }

    @AfterEach
    void clearTenant() {
        TenantContext.clear();
    }

    // ---------- REGISTER USER ----------

    @Test
    void shouldRegisterUserSuccessfully() {
        Login user = new Login();
        user.setUsername("user1");
        user.setPassword("password");

        when(userDao.save(any(Login.class))).thenReturn(user);

        Login result = userService.registerUser(user);

        assertNotNull(result.getPassword());
        assertEquals("tenant1", result.getTenantId());
        verify(userDao).save(user);
    }

    @Test
    void shouldThrowExceptionWhenPasswordBlank() {
        Login user = new Login();
        user.setPassword("");

        assertThrows(IllegalArgumentException.class,
                () -> userService.registerUser(user));
    }

    // ---------- LOGIN / VERIFY ----------

    @Test
    void shouldLoginSuccessfullyAndSetCookies() {
        Login user = new Login();
        user.setUsername("user1");
        user.setPassword("password");

        Login dbUser = new Login();
        dbUser.setUsername("user1");
        dbUser.setTenantId("tenant1");
        dbUser.setId(1);
        dbUser.setRole("USER");

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken("refresh-token");

        when(authManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(userDao.findByUsername("user1")).thenReturn(dbUser);
        when(jwtService.generateAccessToken(any(), any(), any(), any()))
                .thenReturn("access-token");
        when(refreshTokenService.createRefreshToken(any(), any(), any()))
                .thenReturn(refreshToken);
        when(jwtService.getAccessTokenValidity()).thenReturn(900000L);
        when(jwtService.getRefreshTokenValidity()).thenReturn(604800000L);

        ResponseEntity<?> responseEntity =
                userService.verify(user, request, response);

        assertEquals(200, responseEntity.getStatusCodeValue());
        verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
    }

    // ---------- LOGOUT ----------

    @Test
    void shouldLogoutSuccessfully() {
        String result = userService.logout(response);
        assertEquals("Logout successful", result);
        verify(response, times(2)).addHeader(eq("Set-Cookie"), anyString());
    }

    // ---------- REFRESH ACCESS TOKEN ----------

    @Test
    void shouldReturnUnauthorizedWhenRefreshTokenMissing() {
        when(request.getCookies()).thenReturn(null);

        ResponseEntity<String> responseEntity =
                userService.refreshAccessToken(request, response);

        assertEquals(401, responseEntity.getStatusCodeValue());
    }

    // ---------- GET USER BY USERNAME ----------

    @Test
    void shouldGetUserByUsername() {
        Login user = new Login();
        when(userDao.findByUsername("user1")).thenReturn(user);

        Login result = userService.getByUsername("user1");

        assertNotNull(result);
    }
}
