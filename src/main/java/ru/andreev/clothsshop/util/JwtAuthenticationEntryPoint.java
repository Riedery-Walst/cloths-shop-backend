package ru.andreev.clothsshop.util;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        // Возвращаем ошибку 401 - Unauthorized, если токен отсутствует или невалиден
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Access is denied");
    }
}