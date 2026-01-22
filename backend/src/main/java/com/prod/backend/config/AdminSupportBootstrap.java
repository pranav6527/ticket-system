package com.prod.backend.config;

import com.prod.backend.entity.UserEntity;
import com.prod.backend.enums.Role;
import com.prod.backend.repo.UserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSupportBootstrap {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @Value("${admin.email}")
    private final String adminMail;

    @Value("${admin.password}")
    private final String adminPassword;

    @PostConstruct
    public void init() {
        if (repo.findByEmail(adminMail).isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail(adminMail);
            admin.setPassword(encoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            repo.save(admin);
        }
    }
}
