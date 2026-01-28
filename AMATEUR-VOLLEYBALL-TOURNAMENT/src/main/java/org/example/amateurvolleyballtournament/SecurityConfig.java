package org.example.amateurvolleyballtournament;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    // This Bean is needed in order to inject PasswordEncoder into the Service
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // anyone chan do login or registration
                        .requestMatchers("/api/users/login", "/api/users/create", "/api/users/seed").permitAll()
                        // Everything else requires authentication
                        // .anyRequest().authenticated()
                        // Everything open now in order to test
                        .anyRequest().permitAll()
                );
        return http.build();
    }
}
