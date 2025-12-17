package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.T52PunchDto;
import com.example.FabriqBackend.service.impl.DeviceAttendanceServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/v1/device-attendance")
public class DeviceAttendanceController {

    private final DeviceAttendanceServiceImpl deviceAttendanceService;

    @PostMapping
    public ResponseEntity<String> receivePunchData(@RequestBody T52PunchDto punchDto) {
        deviceAttendanceService.processPunch(punchDto);
        return ResponseEntity.ok("Punch data received");
    }
}
