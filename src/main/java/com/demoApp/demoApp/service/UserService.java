package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import  org.slf4j.Logger;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String username_or_mail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmailWithRoles(username_or_mail)
                .orElseThrow(() ->new UsernameNotFoundException("User not found"));

        //DB roles into Spring Security authorities
        List<SimpleGrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(
                                "ROLE_" + role.getName()
                        ))
                        .toList();

        //Role Trace
        user.getRoles().forEach(role ->
                logger.info("User role loaded: {} | {}", role.getName(),UserService.class)
        );

        return new org.springframework.security.core.userdetails.User(
          user.getUsername(),
          user.getPassword(),
          authorities
        );
    }

}
