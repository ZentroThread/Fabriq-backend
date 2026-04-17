package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.DeviceAttendanceLog;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DeviceAttendanceLogDao extends TenantAwareDao<DeviceAttendanceLog, Long> {

    List<DeviceAttendanceLog> findByEmpCodeAndPunchTimeBetween(
            String empCode,
            LocalDateTime startTime,
            LocalDateTime endTime
    );

    List<DeviceAttendanceLog> findByPunchTimeBetween(
            LocalDateTime startOfDay,
            LocalDateTime endOfDay
    );


}
