package com.example.FabriqBackend.service;

import com.example.FabriqBackend.model.Login;

public interface IUserService {
    Login registerUser(Login user);
    String verify(Login user);
    Login getByUsername(String username);
}
