package com.prod.backend.service;

import com.prod.backend.config.JwtProperties;
import com.prod.backend.entity.RefreshTokenEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.repo.RefreshTokenRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProperties jwtProperties;

    private boolean isProd() {
        return "prod".equals(System.getProperty("spring.profiles.active"));
    }

    /* =========================
       Creation (login)
       ========================= */
    @Transactional
    public RefreshTokenEntity create(UserEntity user) {

        RefreshTokenEntity token = new RefreshTokenEntity();
        token.setUser(user);
        token.setToken(UUID.randomUUID().toString());
        token.setExpiryDate(Instant.now().plusMillis(jwtProperties.refresh().expiration()));

        return refreshTokenRepository.save(token);
    }

    /* =========================
       Validation
       ========================= */
    public RefreshTokenEntity validate(String token) {

        RefreshTokenEntity entity = refreshTokenRepository.findByTokenWithUser(token)
                .orElseThrow(() -> new RuntimeException("Invalid refresh token"));

        if (entity.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(entity);
            throw new RuntimeException("Refresh token expired");
        }

        return entity;
    }

    /* =========================
       Rotation
       ========================= */
    @Transactional
    public RefreshTokenEntity rotate(RefreshTokenEntity oldToken) {

        refreshTokenRepository.delete(oldToken);

        RefreshTokenEntity newToken = new RefreshTokenEntity();
        newToken.setUser(oldToken.getUser());
        newToken.setToken(UUID.randomUUID().toString());
        newToken.setExpiryDate(Instant.now().plusMillis(jwtProperties.refresh().expiration()));

        return refreshTokenRepository.save(newToken);
    }

    /* =========================
       Deletion (logout)
       ========================= */

    @Transactional
    public void logout(String refreshTokenStr) {
        refreshTokenRepository.findByToken(refreshTokenStr)
                .ifPresent(refreshTokenRepository::delete);
    }

    /* =========================
       Cookie helpers
       ========================= */
    public ResponseCookie buildCookie(String token) {
        return ResponseCookie.from("refreshToken", token)
                .httpOnly(true)
                .secure(isProd())
                .sameSite(isProd() ? "None" : "Lax")
                .path("/") // dev + prod safe
                .maxAge(Duration.ofMillis(jwtProperties.refresh().expiration()))
                .build();
    }

    public ResponseCookie clearCookie() {
        return ResponseCookie.from("refreshToken", "")
                .httpOnly(true)
                .secure(isProd())
                .sameSite(isProd() ? "None" : "Lax")
                .path("/")
                .maxAge(0)
                .build();
    }

}
