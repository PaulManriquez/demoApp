package com.demoApp.demoApp.config;

import com.demoApp.demoApp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserService userService;

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public SecurityConfig(UserService userService, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{

		// Roles must be updated, for now we will let only admin
		http.authorizeHttpRequests( auth-> auth
				.requestMatchers("/","/home","/login","/css/**","/bootstrap/**","/images/**","/js/**","/favicon.ico").permitAll()
						.requestMatchers("/google/**").hasRole("ADMIN")
						.requestMatchers("/admin/**").hasRole("ADMIN")
						.requestMatchers("/services/**").hasRole("ADMIN")
						.requestMatchers("/appointments/**").hasAnyRole("ADMIN", "TECHNICIAN")
						.requestMatchers("/clients/**").hasAnyRole("ADMIN", "TECHNICIAN")
						.requestMatchers("/users/**").hasRole("ADMIN")
                        .requestMatchers("/roles/**").hasRole("ADMIN")
                .anyRequest().authenticated()
        )
                .formLogin(form->form
                        .loginPage("/login")
                        .successHandler(roleBasedSuccessHandler())
                        .permitAll()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedPage("/access-denied")) /*Go to ErrorController 403*/
                .logout(logout -> logout.permitAll());

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            if (isAdmin) {
                response.sendRedirect("/admin");
                return;
            }

            boolean isTechnician = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_TECHNICIAN".equals(a.getAuthority()));
            if (isTechnician) {
                response.sendRedirect("/appointments");
                return;
            }

            response.sendRedirect("/");
        };
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider =
                new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(userService);
        authProvider.setPasswordEncoder(passwordEncoder);

        return authProvider;
    }
}
