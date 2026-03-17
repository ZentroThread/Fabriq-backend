package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.JwtFilter;
import com.example.FabriqBackend.dto.AttendanceCreateDto;
import com.example.FabriqBackend.dto.AttendanceDto;
import com.example.FabriqBackend.service.IAttendanceService;
import com.example.FabriqBackend.service.JWTService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AttendanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class AttendanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private IAttendanceService attendanceService;

    @Autowired
    private ObjectMapper objectMapper;


    // 1️⃣ Test Mark Attendance
    @Test
    void testMarkAttendance() throws Exception {

        AttendanceCreateDto dto = new AttendanceCreateDto();

        Mockito.doNothing().when(attendanceService).markAttendance(any());

        mockMvc.perform(post("/v1/attendance")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    // 2️⃣ Test Get Monthly Attendance By Employee
    @Test
    void testGetMonthlyByEmp() throws Exception {

        Mockito.when(attendanceService
                        .getMonthlyAttendanceByEmpCode("EMP001", 2025, 1))
                .thenReturn(List.of(new AttendanceDto()));

        mockMvc.perform(get("/v1/attendance/EMP001/month")
                        .param("year", "2025")
                        .param("month", "1"))
                .andExpect(status().isOk());
    }


    // 3️⃣ Test Get All Employees Monthly Attendance
    @Test
    void testGetAllForMonth() throws Exception {

        Mockito.when(attendanceService
                        .fetchAllAttendanceForMonth(2025, 1))
                .thenReturn(List.of(new AttendanceDto()));

        mockMvc.perform(get("/v1/attendance/month")
                        .param("year", "2025")
                        .param("month", "1"))
                .andExpect(status().isOk());
    }


    // 4️⃣ Test Get Attendance By Date
    @Test
    void testGetAttendanceByDate() throws Exception {

        Mockito.when(attendanceService
                        .fetchAllAttendanceForDate("2025-11-01"))
                .thenReturn(List.of(new AttendanceDto()));

        mockMvc.perform(get("/v1/attendance/date")
                        .param("date", "2025-11-01"))
                .andExpect(status().isOk());
    }

}