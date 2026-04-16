package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Login;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends TenantAwareDao<Login, Integer> {

    @Query("SELECT u FROM Login u WHERE u.username = :username")
    Login findByUsername(@Param("username") String username);
}
