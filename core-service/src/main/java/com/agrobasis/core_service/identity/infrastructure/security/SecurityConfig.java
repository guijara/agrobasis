package com.agrobasis.core_service.identity.infrastructure.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/user").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/identity/membership-requests").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/organization").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/organization").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/identity/membership-requests/*/approve").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/identity/membership-requests/*/reject").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/identity/membership-requests/pending").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cost/profiles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cost/profiles/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/cost/freight-profiles").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/cost/freight-profiles/*").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/market/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/market/**").hasRole("ADMIN")
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) ->
                                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.setStatus(HttpServletResponse.SC_FORBIDDEN))
                );

        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
