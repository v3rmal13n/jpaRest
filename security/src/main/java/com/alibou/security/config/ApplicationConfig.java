package com.alibou.security.config;

import com.alibou.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor // позволяет внедрять без аннотаций
public class ApplicationConfig {

    private final UserRepository repository;

    @Bean
    public UserDetailsService userDetailsService () {
        // мы хотим получить имя из бд
        return username -> repository.findByEmail(username)// нами созданный метод поиска по почте
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));// если такой почты нет, то исключение
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        // провайдер извлекает данные пользователя
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());// говорим, какой сервис использовать провайдеру
        authProvider.setPasswordEncoder(passwordEncoder());// раскодировываем пароль и присваиваем
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager (AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}