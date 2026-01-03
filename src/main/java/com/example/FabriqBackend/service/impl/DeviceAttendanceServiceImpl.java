package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.AttendanceDao;
import com.example.FabriqBackend.dao.DeviceAttendanceLogDao;
import com.example.FabriqBackend.dto.T52PunchDto;
import com.example.FabriqBackend.model.DeviceAttendanceLog;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceAttendanceServiceImpl {

    private final DeviceAttendanceLogDao deviceAttendanceLogDao;

    // Process punch data from T52 device
    public void processPunch(T52PunchDto dto){

        String empCode;
        LocalDateTime punchTime;
        String direction;

        if(dto.getEmpCode() != null && dto.getPunchTime() != null){
            // Old firmware
            empCode = dto.getEmpCode();
            punchTime = parseTime(dto.getPunchTime());
            direction = normalizeDirection(dto.getStatus().toString());
        } else {
            // New firmware
            empCode = dto.getUserID();
            punchTime = parseTime(dto.getLogDate());
            direction = normalizeDirection(dto.getDirection());
        }

        DeviceAttendanceLog attendanceLog = new DeviceAttendanceLog();
        attendanceLog.setEmpCode(empCode);
        attendanceLog.setPunchTime(punchTime);
        attendanceLog.setDirection(direction);

        deviceAttendanceLogDao.save(attendanceLog);

    }

    // Normalize direction to "IN" or "OUT"
    private String normalizeDirection(String dir){

        if(dir == null){
            return "IN";
        }
        if(dir.equalsIgnoreCase("IN") || dir.equals("0")){
            return "IN";
        } else if(dir.equalsIgnoreCase("OUT") || dir.equals("1") || dir.equals("2")){
            return "OUT";
        } else {
            return "IN";
        }
    }

    // Parse time from different formats
    private LocalDateTime parseTime(String input) {
        try {
            return LocalDateTime.parse(input);
        } catch (Exception e) {
            DateTimeFormatter f = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            return LocalDateTime.parse(input, f);
        }
    }

//    public List<DeviceAttendanceLog> getLatestLogs(LocalDateTime sinceTime) {
//       // return deviceAttendanceLogDao.findByPunchTimeAfter(sinceTime);
//    }

}
