package org.example.amateurvolleyballtournament;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    // Questo Bean serve per poter iniettare PasswordEncoder nel tuo Service
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Questa configurazione DISABILITA il login di default di Spring
    // così puoi gestire tu il login custom nel tuo Controller/Service
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Disabilita CSRF (per le chiamate Postman)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Permette TUTTE le richieste senza login automatico di Spring
                );
        return http.build();
    }
}
