package com.cityglam.cityglam.config;

import com.cityglam.cityglam.repository.UserRepository;
import com.cityglam.cityglam.util.CustomOAuth2AuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                "/location/countries",
                                "/location/regions",
                                "/location/counties",
                                "/location/districts",
                                "/signin",
                                "/login",
                                "/dashboard",
                                "/api/**"


                        )
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/**").permitAll()
                        .requestMatchers( "/login","/signin",  "/assets/**", "/register", "/dashboard" ,  "/verify-otp", "/auth-two-steps", "/register-multi-step", "/location/countries", "/location/regions", "/location/counties", "/location/districts" , "/set-password" ,"/oauth2/**").permitAll() // Allow login and static resources
                        .anyRequest().authenticated() // Secure all other requests

                )
                .formLogin(form -> form
                        .loginPage("/signin") // Custom login page
                        .loginProcessingUrl("/login")
                        .failureUrl("/signin?error=true") // Redirect on failure
                        .defaultSuccessUrl("/register", true) // Redirect after successful login
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .permitAll()
                )
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login") // Use the same login page for OAuth2
                        .successHandler(oauth2AuthenticationSuccessHandler())
                        .failureUrl("/login?error=true") // Redirect on failure
                )

        ;

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler oauth2AuthenticationSuccessHandler() {
        return new CustomOAuth2AuthenticationSuccessHandler(userRepository);
    }


}



