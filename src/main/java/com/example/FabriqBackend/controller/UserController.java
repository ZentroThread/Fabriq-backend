package com.example.FabriqBackend.controller;

import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.UserPrincipal;
import com.example.FabriqBackend.service.impl.UserServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController //base url for user related operations
@RequestMapping("/v1/user")
@RequiredArgsConstructor//lombok will create constructor for all final fields
public class UserController {


    private final UserServiceImpl userService;

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
    public String login(@RequestBody Login user,  HttpServletResponse response) {
        System.out.println("login request received for user: " + user.getUsername());
        return userService.verify(user,response);
    }

    @GetMapping("/me") //get current user details
    @Operation(
            summary = "Get current user details",
            description = "This endpoint returns the details of the currently authenticated user."
    )
    public Login getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String username = userPrincipal.getUsername();
            return userService.getByUsername(username);
        }
        throw new RuntimeException("User not authenticated");
    }

    @PostMapping("/logout") //logout user and clear cookie
    @Operation(
            summary = "Logout user",
            description = "This endpoint logs out the user by clearing the JWT token cookie."
    )
    public String logout(HttpServletResponse response) {
        return userService.logout(response);
    }

}
