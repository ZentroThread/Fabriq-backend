package com.example.FabriqBackend.dao;

import com.example.FabriqBackend.model.DeviceAttendanceLog;

import java.time.LocalDateTime;
import java.util.List;

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
