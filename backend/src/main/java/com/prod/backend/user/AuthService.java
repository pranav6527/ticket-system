package com.prod.backend.user;

import com.prod.backend.security.JwtUtility;
import com.prod.backend.security.RefreshTokenEntity;
import com.prod.backend.security.RefreshTokenService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final JwtUtility jwtUtility;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RefreshTokenService refreshTokenService;

    public void signup(SignupRequest request) {

        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }


        UserEntity user = new UserEntity();
        user.setEmail(request.getEmail());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRole(Role.USER);
        userRepository.save(user);
    }


    public AuthResponse login(LoginRequest request, HttpServletResponse response) {

        UserEntity user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("Invalid credentials"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials");
        }

        String accessToken = jwtUtility.generateToken(user);

        RefreshTokenEntity refreshToken =
                refreshTokenService.create(user);

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshTokenService.buildCookie(refreshToken.getToken()).toString()
        );

        return new AuthResponse(accessToken);
    }

    public AuthResponse refresh(String refreshTokenStr, HttpServletResponse response) {

        RefreshTokenEntity existing =
                refreshTokenService.validate(refreshTokenStr);

        String newAccessToken =
                jwtUtility.generateToken(existing.getUser());

        RefreshTokenEntity rotated =
                refreshTokenService.rotate(existing);

        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshTokenService.buildCookie(rotated.getToken()).toString()
        );

        return new AuthResponse(newAccessToken);
    }

    public void logout(String refreshTokenStr, HttpServletResponse response) {

        if (refreshTokenStr != null) {
            refreshTokenService.logout(refreshTokenStr);
        }

        // Always clear cookie (even if token missing)
        response.addHeader(
                HttpHeaders.SET_COOKIE,
                refreshTokenService.clearCookie().toString()
        );
    }

}
