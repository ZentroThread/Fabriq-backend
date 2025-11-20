package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
//@AllArgsConstructor
@RequiredArgsConstructor//lombok will create constructor for all final fields
public class UserController {


    private final UserService userService;

    @PostMapping("/register") //register new user
    @Operation(
            summary = "Register a new user",
            description = "This endpoint allows registering a new user by providing the necessary details in the request body."
    )
    public Login register(@RequestBody Login user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login") //login existing user
    @Operation(
            summary = "Login an existing user",
            description = "This endpoint allows an existing user to log in by providing their credentials in the request body."
    )
    public String login(@RequestBody Login user) {
        return userService.verify(user);
    }
}
