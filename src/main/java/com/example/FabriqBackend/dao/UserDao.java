package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends TenantAwareDao<Login, Integer> {

    // Don't filter by tenant for login - we need to get user first to know their tenant
    Login findByUsername(String username);
}
