package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.config.JwtFilter;
import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.service.IEmployeeService;
import com.example.FabriqBackend.service.JWTService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmployeeController.class)
@AutoConfigureMockMvc(addFilters = false)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private JWTService jwtService;

    @MockBean
    private JwtFilter jwtFilter;

    @MockBean
    private IEmployeeService employeeService;


    // 1️⃣ Test Add Employee
    @Test
    void testAddEmployee() throws Exception {

        EmployeeDto dto = new EmployeeDto();

        Mockito.when(employeeService.addEmployee(any(), any())).thenReturn(dto);

        MockMultipartFile image =
                new MockMultipartFile("image", "image.jpg",
                        MediaType.IMAGE_JPEG_VALUE, "test".getBytes());

        mockMvc.perform(multipart("/v1/employees")
                        .file(image)
                        .param("name", "John"))
                .andExpect(status().isCreated());
    }


    // 2️⃣ Test Fetch Employee By Code
    @Test
    void testFetchEmployeeById() throws Exception {

        EmployeeDto dto = new EmployeeDto();

        Mockito.when(employeeService.fetchEmployeeById("EMP001"))
                .thenReturn(dto);

        mockMvc.perform(get("/v1/employees/EMP001"))
                .andExpect(status().isOk());
    }


    // 3️⃣ Test Delete Employee
    @Test
    void testDeleteEmployee() throws Exception {

        Mockito.doNothing().when(employeeService).deleteEmployee("EMP001");

        mockMvc.perform(delete("/v1/employees/EMP001"))
                .andExpect(status().isOk());
    }


    // 4️⃣ Test Update Employee
    @Test
    void testUpdateEmployee() throws Exception {

        EmployeeDto dto = new EmployeeDto();

        Mockito.when(employeeService.updateEmployee(any(), eq("EMP001"), any()))
                .thenReturn(dto);

        MockMultipartFile image =
                new MockMultipartFile("image", "image.jpg",
                        MediaType.IMAGE_JPEG_VALUE, "test".getBytes());

        mockMvc.perform(multipart("/v1/employees/EMP001")
                        .file(image)
                        .param("name", "John")
                        .with(request -> {
                            request.setMethod("PUT");
                            return request;
                        }))
                .andExpect(status().isOk());
    }


    // 5️⃣ Test Fetch All Employees
    @Test
    void testFetchAllEmployees() throws Exception {

        Mockito.when(employeeService.fetchAllEmployees())
                .thenReturn(List.of(new EmployeeDto()));

        mockMvc.perform(get("/v1/employees"))
                .andExpect(status().isOk());
    }


    // 6️⃣ Test Fetch Employees By Role
    @Test
    void testFetchEmployeeByRole() throws Exception {

        Mockito.when(employeeService.fetchEmployeeByRole("manager"))
                .thenReturn(List.of(new EmployeeDto()));

        mockMvc.perform(get("/v1/employees/role/manager"))
                .andExpect(status().isOk());
    }

}