package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.DeductionType;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeductionTypeDao extends JpaRepository<DeductionType, Long> {
}
