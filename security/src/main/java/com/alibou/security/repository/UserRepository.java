package com.alibou.security.repository;

import com.alibou.security.user.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    // Optional - потому, что пользователя с таким логином (почтой) может и не существовать
    // чтобы сделать метод найти по почте, введи готовый метод от JPA findBY и добавь емайл
    Optional<User> findByEmail (String email); // и передаем в качестве аргумента емайл
}
