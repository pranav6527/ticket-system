package com.prod.backend.config;

import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "jwt")
@Validated
public record JwtProperties(@NotNull String secret, Long expiration, Refresh refresh) {
    public record Refresh(
            long expiration
    ) {
    }


}
