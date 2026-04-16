package com.example.FabriqBackend.service.impl;

import lombok.extern.slf4j.Slf4j;

import com.example.FabriqBackend.dao.DeviceAttendanceLogDao;
import com.example.FabriqBackend.dto.T52PunchDto;
import com.example.FabriqBackend.model.DeviceAttendanceLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j

public class DeviceAttendanceServiceImpl {

    private final DeviceAttendanceLogDao deviceAttendanceLogDao;
    private final AttendanceServiceImpl attendanceService;

    public void processPunch(T52PunchDto dto){

        String empCode;
        LocalDateTime punchTime;
        String direction;

        if(dto.getEmpCode() != null && dto.getPunchTime() != null){
            empCode = dto.getEmpCode();
            punchTime = parseTime(dto.getPunchTime());
            direction = normalizeDirection(dto.getStatus().toString());
        } else {
            empCode = dto.getUserID();
            punchTime = parseTime(dto.getLogDate());
            direction = normalizeDirection(dto.getDirection());
        }

        DeviceAttendanceLog attendanceLog = new DeviceAttendanceLog();
        attendanceLog.setEmpCode(empCode);
        attendanceLog.setPunchTime(punchTime);
        attendanceLog.setDirection(direction);

        deviceAttendanceLogDao.save(attendanceLog);

        attendanceService.updateDailyAttendance(
                empCode,
                punchTime.toLocalDate()
        );
    }

    private String normalizeDirection(String dir){

        if(dir == null){
            return "IN";
        }

        if(dir.equalsIgnoreCase("IN") || dir.equals("0")){
            return "IN";
        }
        else if(dir.equalsIgnoreCase("OUT") || dir.equals("1") || dir.equals("2")){
            return "OUT";
        }
        else {
            return "IN";
        }
    }

    private LocalDateTime parseTime(String input) {
        try {
            return LocalDateTime.parse(input);
        } catch (Exception e) {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(input, f);
        }
    }

    public List<DeviceAttendanceLog> getLatestLogs() {
        LocalDate date = LocalDate.now();
        LocalDateTime startTime = date.atTime(8, 0);
        LocalDateTime endTime   = date.atTime(17, 0);
        return deviceAttendanceLogDao.findByPunchTimeBetween(startTime, endTime);
    }
}
