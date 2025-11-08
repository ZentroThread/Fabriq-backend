package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Login;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserDao extends JpaRepository<Login, Integer> {


    Login findByUsername(String username);
}
