package com.zeldaspeedruns.zeldaspeedruns.security;

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
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity)  throws Exception{
        httpSecurity
                .authorizeRequests(requests -> {
                    requests.mvcMatchers("/api/user/register").permitAll();
                })
                .formLogin(formLogin -> {
                    formLogin.loginPage("/user/login");
                });

        return httpSecurity.build();
    }
}
