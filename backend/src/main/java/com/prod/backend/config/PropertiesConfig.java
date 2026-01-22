package com.prod.backend.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({AdminProperties.class, JwtProperties.class})
public class PropertiesConfig {
}
