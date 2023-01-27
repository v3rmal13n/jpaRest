package com.alibou.security.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component// чтобы наш фильтр работал при каждом запросе, расширим определенный класс
@RequiredArgsConstructor // Lombok аннотация, позволяющая создавать полные конструкторы, в том числе и приватным полям
public class JwtAuthenticationFilter extends OncePerRequestFilter { // класс - фильтр аутентификации (Раньше я его называл SecurityConfig

    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(//@NonNull эти поля(запрос, ответ, чейн фильтр, не должны быть пустыми
        @NonNull HttpServletRequest request, // принимающий запрос
        @NonNull HttpServletResponse response, // с помощью ответа, будем перехватывать запрос, извлекать инфу и предоставлять токены и прочую лабуду
        @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization"); // Создаю переменную, чтобы забирать токен от пользователя
        final String jwt;
        final String userEmail;
        // Если заголовок авторизации равно нулю, или если в заголовок не начинается на Bearer (
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // сделайте фильтр по запросу и ответу и не верни ничего
            filterChain.doFilter(request, response);
            return;
        }
        jwt = authHeader.substring(7); // хотим извлечь токен из 7 подстроки потому что "Bearer " здесь 7 символов, считая пробел

        // после извлечения токена, вытащим также из запроса почту пользователя
        // извлекаем из сервиса почту, и передаем в значение поле jwt - сам токен
        userEmail = jwtService.extractUsername(jwt);// todo извлечь имя почту из класса для извлечения токена

        // если почта есть и пользователь пока что не аутентифицирован, т.е. если аутентификация == нул
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // есть ли такой юзер в бд
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);
            // если токен действителен
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // то нам нужно отправить в контекст и сервлет, что все окей
                UsernamePasswordAuthenticationToken authToken
                        = new UsernamePasswordAuthenticationToken(
                                userDetails, null
                );

                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );

                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }
}
