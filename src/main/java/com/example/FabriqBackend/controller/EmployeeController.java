package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.dto.ResponseDto;
import com.example.FabriqBackend.service.EmployeeService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private EmployeeService employeeService;

    @PostMapping
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDto dto){
        employeeService.addEmployee(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201","Employee created successfully"));
    }

    @GetMapping("/{empCode}")
    public ResponseEntity<EmployeeDto> fetchEmployeeById(@PathVariable String empCode){
        EmployeeDto dto = employeeService.fetchEmployeeById(empCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("/{empCode}")
    public ResponseEntity<?> deleteEmployee(@PathVariable String empCode){
        employeeService.deleteEmployee(empCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200","Employee deleted successfully"));
    }

    @PutMapping("/{empCode}")
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDto dto, @PathVariable String empCode){
        employeeService.updateEmployee(dto,empCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200","Employee updated successfully"));
    }

    @GetMapping
    public ResponseEntity<List<EmployeeDto>> fetchAllEmployees(){
        List<EmployeeDto> empList = employeeService.fetchAllEmployees();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empList);
    }

}
