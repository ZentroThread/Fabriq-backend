package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.Holiday;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HolidayDao extends TenantAwareDao<Holiday, Integer> {

    List<Holiday> findByDateBetween(String start, String end);


}
