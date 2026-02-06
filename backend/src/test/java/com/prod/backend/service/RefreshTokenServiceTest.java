package com.prod.backend.service;

import com.prod.backend.config.JwtProperties;
import com.prod.backend.entity.RefreshTokenEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.repo.RefreshTokenRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    private RefreshTokenService refreshTokenService;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-for-tests-only-which-using-for-testing-purpose-and-its-working",
                900000L,
                new JwtProperties.Refresh(60000L)
        );
        refreshTokenService = new RefreshTokenService(refreshTokenRepository, properties);
    }

    @Test
    void buildCookieSetsHttpOnlyAndPath() {
        ResponseCookie cookie = refreshTokenService.buildCookie("token");
        assertEquals("refreshToken", cookie.getName());
        assertEquals("token", cookie.getValue());
        assertEquals("/", cookie.getPath());
        assertNotNull(cookie.getMaxAge());
    }

    @Test
    void clearCookieExpiresImmediately() {
        ResponseCookie cookie = refreshTokenService.clearCookie();
        assertEquals(0, cookie.getMaxAge().getSeconds());
    }

    @Test
    void validateThrowsWhenTokenMissing() {
        when(refreshTokenRepository.findByTokenWithUser("missing"))
                .thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> refreshTokenService.validate("missing"));
    }

    @Test
    void validateDeletesExpiredToken() {
        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setToken("expired");
        entity.setExpiryDate(Instant.now().minusSeconds(10));

        when(refreshTokenRepository.findByTokenWithUser("expired"))
                .thenReturn(Optional.of(entity));

        assertThrows(RuntimeException.class, () -> refreshTokenService.validate("expired"));
        verify(refreshTokenRepository).delete(entity);
    }

    @Test
    void rotateDeletesOldAndCreatesNewToken() {
        UserEntity user = new UserEntity();
        RefreshTokenEntity oldToken = new RefreshTokenEntity();
        oldToken.setToken("old");
        oldToken.setUser(user);
        oldToken.setExpiryDate(Instant.now().plusSeconds(30));

        when(refreshTokenRepository.save(any(RefreshTokenEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        RefreshTokenEntity rotated = refreshTokenService.rotate(oldToken);

        verify(refreshTokenRepository).delete(oldToken);
        assertNotNull(rotated.getToken());
        assertEquals(user, rotated.getUser());
    }
}
