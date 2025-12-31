package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.salary.Holiday;

import java.util.List;

public interface HolidayDao extends TenantAwareDao<Holiday, Integer> {

    List<Holiday> findByDateBetween(String start, String end);


}
