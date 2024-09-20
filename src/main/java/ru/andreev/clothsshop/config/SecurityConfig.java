package ru.andreev.clothsshop.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.andreev.clothsshop.util.JwtTokenFilter;

@Configuration
public class SecurityConfig {

    private final JwtTokenFilter jwtTokenFilter;
    private final AuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(JwtTokenFilter jwtTokenFilter, AuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          UserDetailsService userDetailsService) {
        this.jwtTokenFilter = jwtTokenFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Отключение CSRF
                .csrf(csrf -> csrf.disable())
                // Конфигурация авторизации
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/**", "/api/users/register",  "/api/products/**").permitAll()
                        .requestMatchers("/api/admin/**").authenticated()
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")// Разрешение публичного доступа
                        .anyRequest().authenticated()  // Все остальные запросы требуют аутентификации
                )
                // Указываем на обработчик ошибок аутентификации
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(jwtAuthenticationEntryPoint)
                )
                // Настройка безсессионного управления
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Добавление JWT-фильтра перед стандартным фильтром аутентификации
        http.addFilterBefore(jwtTokenFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}