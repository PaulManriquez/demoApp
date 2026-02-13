package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ?: Receive a user-name and password and check if the user exist in the databse and check
    // if the hashed password correspond to the received
    public boolean login(String userName, String password){

        Optional<User> userOpt = userRepository.findByUsername(userName);

        if(userOpt.isEmpty())
            return false;

        User user = userOpt.get();

        return passwordEncoder.matches(password,user.getPassword());
    }

}
