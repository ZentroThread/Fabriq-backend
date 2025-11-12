package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController

public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register") //register new user
    public Login register(@RequestBody Login user) {
        return userService.registerUser(user);
    }

    @PostMapping("/login") //login existing user
    public String login(@RequestBody Login user) {
        return userService.verify(user);
    }
}
