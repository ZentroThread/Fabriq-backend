package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.Attire;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttireDao  extends TenantAwareDao<Attire, Integer> {

    Attire findByAttireCode(String attireCode);

    List<Attire> findByAttireStatus(String attireStatus);

    List<Attire> findByCategoryCategoryId(Integer categoryId);


}
