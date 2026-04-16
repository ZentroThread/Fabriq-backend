package com.example.FabriqBackend.service.impl;

import com.example.FabriqBackend.dao.CustDao;
import com.example.FabriqBackend.dao.UserDao;
import com.example.FabriqBackend.model.Login;
import com.example.FabriqBackend.model.User;
import com.example.FabriqBackend.model.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class userDetailsService implements UserDetailsService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private CustDao custDao;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Login internalUser = userDao.findByUsername(username);

        if (internalUser != null) {
            return new UserPrincipal(internalUser);
        }

        User customer = custDao.findByEmail(username).orElse(null);

        if (customer != null) {
            return new org.springframework.security.core.userdetails.User(
                    customer.getEmail(),
                    customer.getPassword() != null ? customer.getPassword() : "N/A",
                    List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole()))
            );
        }

        throw new UsernameNotFoundException("User not found: " + username);
    }
}