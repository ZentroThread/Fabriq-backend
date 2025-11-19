package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Login;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends TenantAwareDao<Login, Integer> {
    Login findByUsername(String username);
}
