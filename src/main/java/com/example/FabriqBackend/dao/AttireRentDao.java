package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.AttireRent;
import org.springframework.stereotype.Repository;

@Repository
public interface AttireRentDao extends TenantAwareDao<AttireRent, Integer > {

}
