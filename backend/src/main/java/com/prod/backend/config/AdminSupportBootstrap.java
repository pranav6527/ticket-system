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
    private final AdminProperties superAdmin;

    @PostConstruct
    public void init() {
        if (repo.findByEmail(superAdmin.email()).isEmpty()) {
            UserEntity admin = new UserEntity();
            admin.setEmail(superAdmin.email());
            admin.setPassword(encoder.encode(superAdmin.password()));
            admin.setRole(Role.ADMIN);
            repo.save(admin);
        }
    }
}
