package com.prod.backend.user;

import lombok.Data;

@Data
public class SignupRequest {

    private String email;

    private String password;
}
