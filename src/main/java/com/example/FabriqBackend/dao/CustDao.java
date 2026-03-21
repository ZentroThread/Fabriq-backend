package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CustDao extends JpaRepository<User, Integer> {

    Optional<User> findByEmail(String email);
}
