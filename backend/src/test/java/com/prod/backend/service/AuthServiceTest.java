package com.prod.backend.service;

import com.prod.backend.dto.AuthResponse;
import com.prod.backend.dto.LoginRequest;
import com.prod.backend.dto.SignupRequest;
import com.prod.backend.entity.RefreshTokenEntity;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.enums.Role;
import com.prod.backend.repo.UserRepository;
import com.prod.backend.security.JwtUtility;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseCookie;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private JwtUtility jwtUtility;
    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private RefreshTokenService refreshTokenService;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(jwtUtility, userRepository, passwordEncoder, refreshTokenService);
    }

    @Test
    void signupCreatesUserAndSetsRefreshCookie() {
        SignupRequest request = new SignupRequest();
        request.setEmail("user@example.com");
        request.setPassword("Pass123!");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("Pass123!")).thenReturn("encoded");

        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setRole(Role.USER);
        when(userRepository.save(any(UserEntity.class))).thenReturn(user);

        when(jwtUtility.generateToken(any(UserEntity.class))).thenReturn("access-token");

        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setToken("refresh-token");
        when(refreshTokenService.create(any(UserEntity.class))).thenReturn(refresh);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "refresh-token").build();
        when(refreshTokenService.buildCookie("refresh-token")).thenReturn(cookie);

        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthResponse authResponse = authService.signup(request, response);

        assertEquals("access-token", authResponse.getAccessToken());
        assertNotNull(response.getHeader("Set-Cookie"));
        verify(userRepository).save(any(UserEntity.class));
    }

    @Test
    void signupRejectsDuplicateEmail() {
        SignupRequest request = new SignupRequest();
        request.setEmail("dup@example.com");
        request.setPassword("Pass123!");

        when(userRepository.findByEmail("dup@example.com"))
                .thenReturn(Optional.of(new UserEntity()));

        assertThrows(RuntimeException.class, () -> authService.signup(request, new MockHttpServletResponse()));
    }

    @Test
    void loginReturnsAccessTokenForValidCredentials() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Pass123!");

        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass123!", "encoded")).thenReturn(true);
        when(jwtUtility.generateToken(any(UserEntity.class))).thenReturn("access-token");

        RefreshTokenEntity refresh = new RefreshTokenEntity();
        refresh.setToken("refresh-token");
        when(refreshTokenService.create(user)).thenReturn(refresh);

        ResponseCookie cookie = ResponseCookie.from("refreshToken", "refresh-token").build();
        when(refreshTokenService.buildCookie("refresh-token")).thenReturn(cookie);

        MockHttpServletResponse response = new MockHttpServletResponse();
        AuthResponse authResponse = authService.login(request, response);

        assertEquals("access-token", authResponse.getAccessToken());
        assertNotNull(response.getHeader("Set-Cookie"));
    }

    @Test
    void loginRejectsInvalidPassword() {
        LoginRequest request = new LoginRequest();
        request.setEmail("user@example.com");
        request.setPassword("Pass123!");

        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setPassword("encoded");

        when(userRepository.findByEmail("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("Pass123!", "encoded")).thenReturn(false);

        assertThrows(RuntimeException.class, () -> authService.login(request, new MockHttpServletResponse()));
    }
}
