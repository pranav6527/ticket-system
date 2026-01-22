package com.prod.backend.security;

import com.prod.backend.config.JwtProperties;
import com.prod.backend.entity.UserEntity;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtility {

    private final JwtProperties jwtProperties;


    public String generateToken(UserEntity user) {
        return Jwts.builder()
                .setSubject(user.getEmail())
                .claim("role", user.getRole().name())
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + jwtProperties.expiration()))
                .signWith(Keys.hmacShaKeyFor(jwtProperties.secret().getBytes()))
                .compact();
    }

    public String extractEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(jwtProperties.secret().getBytes())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }


}
