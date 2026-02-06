package com.prod.backend.security;

import com.prod.backend.config.JwtProperties;
import com.prod.backend.entity.UserEntity;
import com.prod.backend.enums.Role;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JwtUtilityTest {

    private JwtUtility jwtUtility;

    @BeforeEach
    void setUp() {
        JwtProperties properties = new JwtProperties(
                "test-secret-for-tests-only-which-using-for-testing-purpose-and-its-working",
                60000L,
                new JwtProperties.Refresh(60000L)
        );
        jwtUtility = new JwtUtility(properties);
        jwtUtility.init();
    }

    @Test
    void generateTokenAndExtractEmail() {
        UserEntity user = new UserEntity();
        user.setEmail("user@example.com");
        user.setRole(Role.USER);

        String token = jwtUtility.generateToken(user);

        assertNotNull(token);
        assertEquals("user@example.com", jwtUtility.extractEmail(token));
    }
}
