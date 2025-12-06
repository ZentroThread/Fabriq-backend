package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.AllowanceType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AllowanceTypeDao extends JpaRepository<AllowanceType, Long> {
}
