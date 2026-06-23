//package com.sbms.config;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
//
//@Configuration
//public class SecurityConfig {
//
//    @Bean
//    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//
//        http
//            // CORS enable (must)
//            .cors(Customizer.withDefaults())
//
//            // For now: disable CSRF for APIs (dev)
//            .csrf(csrf -> csrf.disable())
//
//            // Allow all requests for now (we will secure later in auth sprint)
//            .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
//
//        return http.build();
//    }
//}