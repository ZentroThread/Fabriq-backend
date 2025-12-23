package com.example.FabriqBackend.service;

import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class userDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Login users = userDao.findByUsername(username);
        if (users == null) {
            throw new UsernameNotFoundException("Username not found");
        }

        if (users.getPassword() == null || users.getPassword().isEmpty()) {
            throw new UsernameNotFoundException("User password not set");
        }
        System.out.println("User found: " + users.getUsername());

        return new UserPrincipal(users);
    }
}
