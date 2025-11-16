package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceDao extends JpaRepository<Attendance, Long> {

    Optional<List<Attendance>> findByEmpCodeAndDate(String empCode, LocalDate date);
    Optional<List<Attendance>> findByEmpCodeAndDateBetweenOrderByTimeAsc(String empCode,LocalDate from,LocalDate to);
    Optional<List<Attendance>> findByDateBetweenOrderByTimeAsc(LocalDate from, LocalDate to);
    Optional<List<Attendance>> findByDate(LocalDate date);
}
