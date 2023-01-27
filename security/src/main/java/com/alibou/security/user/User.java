package com.alibou.security.user;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Data // Lombok аннотация, которая предоставляет нам сеттеры и геттеры
@Builder // constructor
@NoArgsConstructor
@AllArgsConstructor // constructor
@Entity
@Table(name = "_user")
public class User implements UserDetails { // Даем Security доступ к ентити
    @Id
    @Column(name = "id")
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    private String email;
    private String password;

    @Enumerated(EnumType.STRING) // Даем понять security, что эта переменная - это одно из значений из enum, также даем понять, что в перечислении строки
    private Role role; // Создаем роли, в виде чисел, в enum классе

    @Override // метод по предоставлению ролей
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name())); // SimpleGrantedAuthority класс, который хранит String роли, а .name -  метод, который предоставляет имя роли, например role ADMIN
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email; // для нас, наш логин - электронная почта, этот метод будет возвращать электронную почту
    }

    @Override
    public boolean isAccountNonExpired() { // срок действия аккаунта истек
        return true; //срок действия не истек - это отрицание, поэтому нужно возвращать правду
    }

    @Override
    public boolean isAccountNonLocked() { // заблокирован
        return true; // аккаунты и правда не заблокированы
    }

    @Override
    public boolean isCredentialsNonExpired() { // срок действия аккаунта НЕ истек
        return true;
    }

    @Override
    public boolean isEnabled() { // аккаунт включен
        return true;
    }
}
