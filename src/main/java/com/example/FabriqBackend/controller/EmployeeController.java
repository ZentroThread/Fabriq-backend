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
public class EmployeeController {

    private EmployeeService employeeService;

    @PostMapping("/addEmployee")
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDto dto){
        employeeService.addEmployee(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto("201","Employee created successfully"));
    }

    @GetMapping("/getEmployees/{empId}")
    public ResponseEntity<EmployeeDto> fetchEmployeeById(@PathVariable Long empId){
        EmployeeDto dto = employeeService.fetchEmployeeById(empId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("/deleteEmployee/{empId}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long empId){
        employeeService.deleteEmployee(empId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200","Employee deleted successfully"));
    }

    @PutMapping("/updateEmployee/{empId}")
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDto dto, @PathVariable Long empId){
        employeeService.updateEmployee(dto,empId);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto("200","Employee updated successfully"));
    }

    @GetMapping("getEmployees")
    public ResponseEntity<List<EmployeeDto>> fetchAllEmployees(){
        List<EmployeeDto> empList = employeeService.fetchAllEmployees();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empList);
    }

}
