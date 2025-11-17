package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Measurement;
import org.springframework.stereotype.Repository;

@Repository
public interface MeasurementDao extends TenantAwareDao<Measurement,Integer> {


}
