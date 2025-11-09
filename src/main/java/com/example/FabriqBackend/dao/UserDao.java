package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface UserDao extends TenantAwareDao<Login, Integer> {

    @Query("SELECT l FROM Login l WHERE l.username = ?1 AND l.tenantId = ?#{T(com.example.FabriqBackend.config.TenantContext).getCurrentTenant()}")
    Login findByUsername(String username);

    //Login findByUsername(String username);
}
