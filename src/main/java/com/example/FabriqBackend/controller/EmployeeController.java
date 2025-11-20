package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.dto.EmployeeDto;
import com.example.FabriqBackend.dto.ResponseDto;
import com.example.FabriqBackend.service.IEmployeeService;
import com.example.FabriqBackend.service.impl.EmployeeServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/v1/employees")
public class EmployeeController {

    private IEmployeeService employeeService;

    @PostMapping
    @Operation(
            summary = "Add a new employee",
            description = "This endpoint allows adding a new employee by providing the necessary details in the request body."
    )
    public ResponseEntity<?> addEmployee(@RequestBody EmployeeDto dto){
        employeeService.addEmployee(dto);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(dto);
    }

    @GetMapping("/{empCode}")
    @Operation(
            summary = "Fetch employee by employee code",
            description = "This endpoint retrieves the details of an employee by their employee code."
    )
    public ResponseEntity<EmployeeDto> fetchEmployeeById(@PathVariable String empCode){
        EmployeeDto dto = employeeService.fetchEmployeeById(empCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @DeleteMapping("/{empCode}")
    @Operation(
            summary = "Delete an employee",
            description = "This endpoint allows deleting an employee by their employee code."
    )
    public ResponseEntity<?> deleteEmployee(@PathVariable String empCode){
        employeeService.deleteEmployee(empCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empCode);
    }

    @PutMapping("/{empCode}")
    @Operation(
            summary = "Update an employee's details",
            description = "This endpoint allows updating the details of an existing employee by their employee code."
    )
    public ResponseEntity<?> updateEmployee(@RequestBody EmployeeDto dto, @PathVariable String empCode){
        employeeService.updateEmployee(dto,empCode);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(dto);
    }

    @GetMapping
    @Operation(
            summary = "Fetch all employees",
            description = "This endpoint retrieves a list of all employees."
    )
    public ResponseEntity<List<EmployeeDto>> fetchAllEmployees(){
        List<EmployeeDto> empList = employeeService.fetchAllEmployees();
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empList);
    }

    @GetMapping("/role/{role}")
    @Operation(
            summary = "Fetch Employees using role",
            description = "This endpoint helps to find employees using their role"
    )
    public ResponseEntity<List<EmployeeDto>> fetchEmployeeByRole(@PathVariable String role){
        List<EmployeeDto> empList = employeeService.fetchEmployeeByRole(role);
        if(empList.isEmpty()){
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(empList);
    }

}
