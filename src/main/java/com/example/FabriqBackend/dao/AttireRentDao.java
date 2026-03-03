package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.AttireRent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AttireRentDao extends TenantAwareDao<AttireRent, Integer> {

    AttireRent findByIdAndTenantId(Integer id, String tenantId);

    List<AttireRent> findAllByTenantId(String tenantId);


    List<AttireRent> findAllByBillingCode(String billingCode);

    List<AttireRent> findByAttireId(Integer id);

    @Query("""
        SELECT ar FROM AttireRent ar
        WHERE ar.attireCode = :attireCode
        AND ar.returnDate >= :blockedFrom
    """)
    List<AttireRent> findConflictingRents(
            @Param("attireCode") String attireCode,
            @Param("blockedFrom") LocalDateTime blockedFrom
    );

    List<AttireRent> findAllByAttireCode(String attireCode);

//    List<AttireRent> findAllByAttireCodeAndRentDateAfter(String attireCode, java.time.LocalDateTime dateTime);
//
//    List<AttireRent> findAllByAttireCodeAndRentDateBefore(String attireCode, java.time.LocalDateTime dateTime);
}
