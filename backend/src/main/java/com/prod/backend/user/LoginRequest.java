package com.prod.backend.user;

import lombok.Data;

@Data
public class LoginRequest {

    private String email;

    private String password;
}
