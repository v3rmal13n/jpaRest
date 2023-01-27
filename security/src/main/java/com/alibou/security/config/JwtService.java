package com.alibou.security.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    private static final String SECRET_KEY = "753778214125442A462D4A614E645267556B58703273357638792F423F452848\n";

    // Вернет имя пользователя и примет в качестве параметра токен
    public String extractUsername (String token) {
        //извлекаем имя пользователя из токена
        return extractClaim(token, Claims::getSubject);
    }
    // Метод извлечения одного утверждения
    public <T> T extractClaim (String token, Function<Claims, T> claimsResolver) {
        final Claims claim = extractAllClaims(token); // извлеките все утверждения из токена
        return claimsResolver.apply(claim);
    }


    //хотим сгенерировать токен из самих данных пользователя, поэтому создадим новый метод
    public String generateToken (UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }


    // Создаем метод, который будет генерировать токен
    public String generateToken (
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        return Jwts
                .builder()
                .setClaims(extraClaims) // нами созданный МАР туда
                .setSubject(userDetails.getUsername()) // емайл туда
                .setIssuedAt(new Date(System.currentTimeMillis())) // когда была подана заявка
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 24)) // мой токен будет годным такое время
                .signWith(getSignInKey(), SignatureAlgorithm.HS256) // указываем в качестве типа подписи, нами созданный метод и сигнатуру алгоритма подписи
                .compact(); // сгенерирует и вернет токен
    }

    // Метод, который будет проверять валидный ли токен, он будет принимать в качестве параметров сам токен и данные пользователя
    public boolean isTokenValid (String token, UserDetails userDetails) {
        final String username = extractUsername(token); // извлекаем имя пользователя нашим методом из токена
        // а потом сравниваем, равно ли имя пользователя, который в токене, с тем, что входной
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token); //также нужно проверить не истек ли срок действия токена isTokenExpired
    }

    // Метод, который проверяет, истек ли срок действия токена
    private boolean isTokenExpired (String token) {
        return extractExpiration(token).before(new Date()); // вернет дату
    }

    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    // класс из зависимости. Метод извлечения всех утверждений
    private Claims extractAllClaims (String token) {
        return Jwts
                .parserBuilder() // парс конструктора
                .setSigningKey(getSignInKey()) // присвоить, сгенерировать, декодировать токен
                .build() //строим этот конструктор и можем использовать метод
                .parseClaimsJws(token) // как токен будет проанализирован
                .getBody(); //в теле получаем все утверждения, которые есть в этом токене
    }

    // этот метод возвращает уже не байт, а декодированный ключ
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); //Декодируем секретный ключ
        return Keys.hmacShaKeyFor(keyBytes); // все что осталось сделать - это правильно передать байты ключа
    }
}

/*
   setSigningKey(getSignInKey()
    берем сгенерированный токен с интернета и присваиваем в качестве ключа
*/