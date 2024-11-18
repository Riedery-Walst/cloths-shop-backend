package ru.andreev.clothsshop.util;

import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.andreev.clothsshop.service.CustomUserDetailsService;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenUtil jwtTokenUtil;
    private final CustomUserDetailsService customUserDetailsService;

    public JwtTokenFilter(JwtTokenUtil jwtTokenUtil, CustomUserDetailsService customUserDetailsService) {
        this.jwtTokenUtil = jwtTokenUtil;
        this.customUserDetailsService = customUserDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain) throws IOException, ServletException {
        final String authorizationHeader = request.getHeader("Authorization");

        String token = null;
        String username = null;

        // Проверяем наличие токена в заголовке
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7);  // Извлекаем токен из заголовка
            try {
                username = jwtTokenUtil.extractEmail(token);  // Извлекаем имя пользователя из токена
            } catch (ExpiredJwtException e) {
                // Логирование ошибки и возврат ошибки 401, если токен истек
                logger.error("Token is expired", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token expired");
                return;
            } catch (Exception e) {
                // Логирование ошибки (опционально)
                logger.error("Token is missing or invalid", e);
            }
        }

        // Проверка аутентификации пользователя
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            try {
                var userDetails = customUserDetailsService.loadUserByUsername(username);

                // Если токен валиден, аутентифицируем пользователя
                if (jwtTokenUtil.validateToken(token, userDetails.getUsername())) {
                    UsernamePasswordAuthenticationToken authenticationToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authenticationToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));

                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                } else {
                    // Токен невалиден, возвращаем ошибку 401
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token");
                    return;
                }
            } catch (Exception e) {
                logger.error("Error processing token", e);
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized: Invalid token");
                return;
            }
        }

        filterChain.doFilter(request, response);  // Пропускаем запрос дальше в цепочку фильтров
    }
}