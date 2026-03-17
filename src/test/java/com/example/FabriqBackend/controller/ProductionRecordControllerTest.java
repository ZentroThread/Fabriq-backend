package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.JwtFilter;
import com.example.FabriqBackend.dto.salary.ProductionRecordRequestDTO;
import com.example.FabriqBackend.dto.salary.ProductionRecordResponseDTO;
import com.example.FabriqBackend.service.JWTService;
import com.example.FabriqBackend.service.impl.ProductionRecordServiceImpl;
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

import static org.mockito.ArgumentMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductionRecordController.class)
@AutoConfigureMockMvc(addFilters = false)
class ProductionRecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private ProductionRecordServiceImpl productionRecordServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;


    // 1️⃣ Test Create Production Record
    @Test
    void testCreateRecord() throws Exception {

        ProductionRecordRequestDTO dto = new ProductionRecordRequestDTO();

        Mockito.when(productionRecordServiceImpl.createProductionRecord(any()))
                .thenReturn(dto);

        mockMvc.perform(post("/v1/production-records")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(dto)))
                .andExpect(status().isOk());
    }


    // 2️⃣ Test Get All Records
    @Test
    void testGetAllRecords() throws Exception {

        Mockito.when(productionRecordServiceImpl.getAllProductionRecords())
                .thenReturn(List.of(new ProductionRecordResponseDTO()));

        mockMvc.perform(get("/v1/production-records"))
                .andExpect(status().isOk());
    }


    // 3️⃣ Test Get Records By Date Range
    @Test
    void testGetByDateRange() throws Exception {

        Mockito.when(productionRecordServiceImpl
                        .getRecordsByDateRange("2025-01-01","2025-01-31"))
                .thenReturn(List.of(new ProductionRecordResponseDTO()));

        mockMvc.perform(get("/v1/production-records/date-range")
                        .param("startDate","2025-01-01")
                        .param("endDate","2025-01-31"))
                .andExpect(status().isOk());
    }


    // 4️⃣ Test Get Records By Employee
    @Test
    void testGetByEmployee() throws Exception {

        Mockito.when(productionRecordServiceImpl
                        .getRecordsByEmployeeId(1L))
                .thenReturn(List.of(new ProductionRecordResponseDTO()));

        mockMvc.perform(get("/v1/production-records/employee/1"))
                .andExpect(status().isOk());
    }


    // 5️⃣ Test Get Records By Employee + Date Range
    @Test
    void testGetByEmployeeAndDateRange() throws Exception {

        Mockito.when(productionRecordServiceImpl
                        .getRecordsByDataRangeAndEmpId("2025-01-01","2025-01-31",1L))
                .thenReturn(List.of(new ProductionRecordResponseDTO()));

        mockMvc.perform(get("/v1/production-records/employee/1/date-range")
                        .param("startDate","2025-01-01")
                        .param("endDate","2025-01-31"))
                .andExpect(status().isOk());
    }


    // 6️⃣ Test Delete Production Record
    @Test
    void testDeleteRecord() throws Exception {

        Mockito.doNothing()
                .when(productionRecordServiceImpl)
                .deleteProductionRecordById(1L);

        mockMvc.perform(delete("/v1/production-records/1"))
                .andExpect(status().isOk());
    }


    // 7️⃣ Test Update Production Record
    @Test
    void testUpdateRecord() throws Exception {

        ProductionRecordResponseDTO responseDTO = new ProductionRecordResponseDTO();

        Mockito.when(productionRecordServiceImpl
                        .updateProductionRecord(eq(1L), any()))
                .thenReturn(responseDTO);

        ProductionRecordRequestDTO requestDTO = new ProductionRecordRequestDTO();

        mockMvc.perform(put("/v1/production-records/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDTO)))
                .andExpect(status().isOk());
    }

}