package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.DeviceAttendanceLogDao;
import com.example.FabriqBackend.dto.T52PunchDto;
import com.example.FabriqBackend.model.DeviceAttendanceLog;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DeviceAttendanceServiceImplTest {

    @Mock
    private DeviceAttendanceLogDao deviceAttendanceLogDao;

    @InjectMocks
    private DeviceAttendanceServiceImpl deviceAttendanceService;

    // ---------------- OLD FIRMWARE ----------------

    @Test
    void shouldProcessPunchFromOldFirmware() {
        T52PunchDto dto = new T52PunchDto();
        dto.setEmpCode("EMP001");
        dto.setPunchTime("2024-12-01T09:00:00");
        dto.setStatus(0); // IN

        deviceAttendanceService.processPunch(dto);

        verify(deviceAttendanceLogDao).save(any(DeviceAttendanceLog.class));
    }

    // ---------------- NEW FIRMWARE ----------------

    @Test
    void shouldProcessPunchFromNewFirmware() {
        T52PunchDto dto = new T52PunchDto();
        dto.setUserID("EMP002");
        dto.setLogDate("2024-12-01 17:00:00");
        dto.setDirection("OUT");

        deviceAttendanceService.processPunch(dto);

        verify(deviceAttendanceLogDao).save(any(DeviceAttendanceLog.class));
    }

    // ---------------- DIRECTION NORMALIZATION ----------------

    @Test
    void shouldNormalizeDirectionToIN() {
        T52PunchDto dto = new T52PunchDto();
        dto.setEmpCode("EMP003");
        dto.setPunchTime("2024-12-01T09:05:00");
        dto.setStatus(0); // ✅ REQUIRED for old firmware

        deviceAttendanceService.processPunch(dto);

        verify(deviceAttendanceLogDao).save(
                argThat(log -> log.getDirection().equals("IN"))
        );
    }


    @Test
    void shouldNormalizeDirectionToOUT() {
        T52PunchDto dto = new T52PunchDto();
        dto.setUserID("EMP004");
        dto.setLogDate("2024-12-01 18:00:00");
        dto.setDirection("1"); // OUT

        deviceAttendanceService.processPunch(dto);

        verify(deviceAttendanceLogDao).save(
                argThat(log -> log.getDirection().equals("OUT"))
        );
    }

    // ---------------- TIME PARSING ----------------

    @Test
    void shouldParseIsoDateTimeCorrectly() {
        T52PunchDto dto = new T52PunchDto();
        dto.setEmpCode("EMP005");
        dto.setPunchTime("2024-12-01T08:55:00");
        dto.setStatus(0);

        deviceAttendanceService.processPunch(dto);

        verify(deviceAttendanceLogDao).save(
                argThat(log -> log.getPunchTime() instanceof LocalDateTime)
        );
    }

    @Test
    void shouldParseCustomDateTimeFormatCorrectly() {
        T52PunchDto dto = new T52PunchDto();
        dto.setUserID("EMP006");
        dto.setLogDate("2024-12-01 08:55:00");
        dto.setDirection("IN");

        deviceAttendanceService.processPunch(dto);

        verify(deviceAttendanceLogDao).save(
                argThat(log -> log.getPunchTime() instanceof LocalDateTime)
        );
    }
}
