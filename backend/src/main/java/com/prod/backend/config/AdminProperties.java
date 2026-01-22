package com.prod.backend.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@ConfigurationProperties(prefix = "admin")
@Validated
public record AdminProperties(
      @NotNull @NotBlank String email,
      @NotNull  @NotBlank String password
) {}
