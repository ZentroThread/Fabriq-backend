package com.example.FabriqBackend.service;

import com.example.FabriqBackend.config.TenantContext;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.UUID;

@Service
@CacheConfig(cacheNames = "users")
public class UserService {

    @Autowired
    private JWTService jwtService;

    @Autowired
    AuthenticationManager authManager;



    @Autowired
    private UserDao userDao;

    private BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

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

        // Use cached path
        Login stored = getByUsername(user.getUsername());
        Authentication authentication = authManager.authenticate(
            new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword())
        );
        if (authentication.isAuthenticated()) {
            // Get the authenticated user's tenant ID
            Login authenticatedUser = userDao.findByUsername(user.getUsername());
            return jwtService.generateToken(user.getUsername(), authenticatedUser.getTenantId());
        } else {
            return "fail";
        }
    }

    // Read by tenant + username
    @Cacheable(key = "T(com.example.FabriqBackend.config.TenantContext).getCurrentTenant() + ':' + #username")
    public Login getByUsername(String username) {
        return userDao.findByUsername(username);
    }
}
