package com.prod.backend.user;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminSupportBootstrap {

    private final UserRepository repo;
    private final PasswordEncoder encoder;

    @PostConstruct
    public void init() {
        if (repo.findByEmail("admin@gmail.com").isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail("admin@gmail.com");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            repo.save(admin);
        }
    }
}
