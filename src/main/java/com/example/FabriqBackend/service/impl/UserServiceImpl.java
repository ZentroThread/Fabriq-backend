package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.config.Tenant.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.service.IUserService;
import com.example.FabriqBackend.service.JWTService;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "users")
public class UserServiceImpl implements IUserService {


    private final JWTService jwtService;
    final AuthenticationManager authManager;
    private final UserDao userDao;


    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    @CachePut(key = "#result.tenantId + ':' + #result.username")
    public Login registerUser(Login user) {
        if (!StringUtils.hasText(user.getPassword())) {
            throw new IllegalArgumentException("Password cannot be blank");
        }

        String tenantId = TenantContext.getCurrentTenant();
        if (!StringUtils.hasText(tenantId)) {
            throw new IllegalArgumentException("Tenant ID is required. Please provide X-Tenant-ID header.");
        }

        user.setPassword(encoder.encode(user.getPassword()));
        user.setTenantId(tenantId);
        userDao.save(user);
        return user;
    }

    public String verify(Login user) {

        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            // Get the authenticated user's details
            Login authenticatedUser = userDao.findByUsername(user.getUsername());
            String token =  jwtService.generateToken(
                user.getUsername(), 
                authenticatedUser.getTenantId(),
                authenticatedUser.getId(),
                authenticatedUser.getRole()
            );
            System.out.println("Generated JWT Token: " + token);
           return token;
        } else {
            return "fail";
        }
    }

    // Read by tenant + username
    @Cacheable(key = "T(com.example.FabriqBackend.config.Tenant.TenantContext).getCurrentTenant() + ':' + #username + ':retrieved by username'")
    public Login getByUsername(String username) {
        return userDao.findByUsername(username);
    }

}
