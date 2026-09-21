package com.kadalVazhi.nn_home_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security configuration for Stateless Microservice.
 *
 * WHY THIS CONFIGURATION:
 * 1. CSRF disabled: In REST APIs with token/header authentication, CSRF tokens are unnecessary.
 * 2. STATELESS session management: No HTTP session cookies are held in memory.
 * 3. BCrypt: One-way salted adaptive hashing for passwords.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12); // Salt rounds = 12 (OWASP recommended balance)
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // Allow Swagger UI & OpenAPI Docs
                .requestMatchers(
                    "/v3/api-docs/**",
                    "/swagger-ui/**",
                    "/swagger-ui.html"
                ).permitAll()
                // Allow Actuator health checks for Kubernetes
                .requestMatchers("/actuator/**").permitAll()
                // Allow registration & public profile endpoints
                .requestMatchers("/api/v1/users/register").permitAll()
                .requestMatchers("/api/v1/users/**").permitAll() // Open for development; will be locked with JWT filter next
                .anyRequest().authenticated()
            );

        return http.build();
    }
}
