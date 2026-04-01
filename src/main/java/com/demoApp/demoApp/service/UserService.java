package com.demoApp.demoApp.service;

import com.demoApp.demoApp.entity.User;
import com.demoApp.demoApp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.util.List;

@Service
public class UserService implements UserDetailsService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found";
    private static final String NO_AUTHENTICATED_USER_MESSAGE = "No authenticated user in security context";

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private static final Logger logger =
            LoggerFactory.getLogger(UserService.class);

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = getUserByUsernameOrEmail(usernameOrEmail);

        //DB roles into Spring Security authorities
        List<SimpleGrantedAuthority> authorities =
                user.getRoles()
                        .stream()
                        .map(role -> new SimpleGrantedAuthority(
                                "ROLE_" + role.getName()
                        ))
                        .toList();

        logger.debug("Loaded user '{}' with {} roles", user.getUsername(), user.getRoles().size());

        return new org.springframework.security.core.userdetails.User(
          user.getUsername(),
          user.getPassword(),
          authorities
        );
    }

    public User findUserByUsernameOrEmail(String usernameOrEmail) throws UsernameNotFoundException {
        return getUserByUsernameOrEmail(usernameOrEmail);
    }

    public User getCurrentlyAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            throw new UsernameNotFoundException(NO_AUTHENTICATED_USER_MESSAGE);
        }

        User user = findUserByUsernameOrEmail(auth.getName());
        logger.debug("Resolved currently authenticated user '{}'", user.getUsername());
        return user;
    }

    private User getUserByUsernameOrEmail(String usernameOrEmail) {
        return userRepository.findByUsernameOrEmailWithRoles(usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(USER_NOT_FOUND_MESSAGE));
    }

}
