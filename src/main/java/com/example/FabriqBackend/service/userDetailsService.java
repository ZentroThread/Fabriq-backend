package com.example.FabriqBackend.service;

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
    private UserDao userDao;   // internal users

    @Autowired
    private CustDao custDao;   // Google users

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       // System.out.println(" Trying to load user: " + username);

        //  Try internal users (username login)
        Login internalUser = userDao.findByUsername(username);

        if (internalUser != null) {
           // System.out.println(" Found INTERNAL user: " + internalUser.getUsername());
            return new UserPrincipal(internalUser);
        }

        //  Try Google / customer users (email login)
        User customer = custDao.findByEmail(username).orElse(null);

        if (customer != null) {
            //System.out.println(" Found CUSTOMER user: " + customer.getEmail());

            return new org.springframework.security.core.userdetails.User(
                    customer.getEmail(),
                    customer.getPassword() != null ? customer.getPassword() : "N/A",
                    List.of(new SimpleGrantedAuthority("ROLE_" + customer.getRole()))
            );
        }

        //  Not found anywhere
        throw new UsernameNotFoundException("User not found: " + username);
    }
}