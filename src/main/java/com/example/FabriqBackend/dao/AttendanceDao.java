package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Attendance;


import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AttendanceDao extends TenantAwareDao<Attendance, Long> {

    List<Attendance> findByDate(LocalDate date);

    List<Attendance> findByEmployee_EmpCodeAndDateBetweenOrderByDateAsc(
            String empCode,
            LocalDate startDate,
            LocalDate endDate
    );
    List<Attendance> findByDateBetweenOrderByDateAsc(
            LocalDate startDate,
            LocalDate endDate
    );

    Optional<Attendance> findByEmployee_EmpCodeAndDate(String empCode, LocalDate date);

    Optional<List<Attendance>> findByEmployee_IdAndDateBetween(Long id, LocalDate startDate, LocalDate endDate);

}
